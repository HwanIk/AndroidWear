/**
 * Copyright 2014 Daum Kakao Corp.
 *
 * Redistribution and modification in source or binary forms are not permitted without specific prior written permission. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kakao.sample.push;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.UUID;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;
import com.kakao.APIErrorResult;
import com.kakao.LogoutResponseCallback;
import com.kakao.PushDeregisterHttpResponseHandler;
import com.kakao.PushMessageBuilder;
import com.kakao.PushRegisterHttpResponseHandler;
import com.kakao.PushSendHttpResponseHandler;
import com.kakao.PushService;
import com.kakao.PushTokenInfo;
import com.kakao.PushTokensHttpResponseHandler;
import com.kakao.Session;
import com.kakao.UnlinkResponseCallback;
import com.kakao.UserManagement;
import com.kakao.helper.Logger;
import com.kakao.helper.SharedPreferencesCache;
import com.kakao.widget.PushActivity;

/**
 * {@link com.kakao.widget.PushActivity}를 이용하여 푸시토큰 등록/삭제, 나에게 푸시 메시지 보내기를 테스트 한다.
 * 유효한 세션이 있다는 검증을 {@link PushLoginActivity}로 부터 받고 보여지는 로그인 된 페이지이다.
 * @author MJ
 */
public class PushMainActivity extends PushActivity {
    protected static final String PROPERTY_DEVICE_ID = "device_id";

    /**
     * [주의!] 아래 예제는 샘플앱에서 사용되는 것으로 기기정보 일부가 포함될 수 있습니다. 실제 릴리즈 되는 앱에서 사용하기 위해서는 사용자로부터 개인정보 취급에 대한 동의를 받으셔야 합니다.
     *
     * 한 사용자에게 여러 기기를 허용하기 위해 기기별 id가 필요하다.
     * ANDROID_ID가 기기마다 다른 값을 준다고 보장할 수 없어, 보완된 로직이 포함되어 있다.
     * @return 기기의 unique id
     */
    protected String getDeviceUUID() {
        if(deviceUUID != null)
            return deviceUUID;

        final SharedPreferencesCache cache = Session.getAppCache();
        final String id = cache.getString(PROPERTY_DEVICE_ID);

        if (id != null) {
            deviceUUID = id;
            return deviceUUID;
        } else {
            UUID uuid = null;
            final String androidId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
            try {
                if (!"9774d56d682e549c".equals(androidId)) {
                    uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
                } else {
                    final String deviceId = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
                    uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
                }
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            Bundle bundle = new Bundle();
            bundle.putString(PROPERTY_DEVICE_ID, uuid.toString());
            cache.save(bundle);

            deviceUUID = uuid.toString();
            return deviceUUID;
        }
    }

    /**
     * {@link com.kakao.widget.PushActivity#onCreate(android.os.Bundle)}에서 GCM으로부터 푸시 토큰을 얻어 카카오 푸시 서버에 등록하고 이를 SharedPreference에 저장한다.
     *
     * @param savedInstanceState activity 내려갈 때 저장해둔 정보
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    /**
     * 나에게 푸시 보내기 버튼, 명시적 푸시 토큰 삭제 버튼, 명시적 푸시 토큰 등록 버튼, 로그아웃(암묵적 푸시토큰 삭제) 버튼에 대한 처리를 진행한다.
     *
     * @param view 클릭된 view
     */
    public void onClick(final View view) {
        if (view == findViewById(R.id.send_button)) {
            sendPushMessageToMe();
        } else if (view == findViewById(R.id.unregistger_button)) {
            deregisterPushToken();
        } else if (view == findViewById(R.id.registger_button)) {
            registerPushToken(new PushRegisterHttpResponseHandler() {
                @Override
                protected void onHttpSuccess(final Integer expiredAt) {
                    super.onHttpSuccess(expiredAt);
                    Toast.makeText(getApplicationContext(), "succeeded to register push token", Toast.LENGTH_SHORT).show();
                }

                @Override
                protected void onHttpSessionClosedFailure(APIErrorResult errorResult) {
                    redirectLoginActivity();
                }

                @Override
                protected void onHttpFailure(APIErrorResult errorResult) {
                    super.onHttpFailure(errorResult);
                    Toast.makeText(getApplicationContext(), errorResult.toString(), Toast.LENGTH_LONG).show();
                }
            });
        } else if (view == findViewById(R.id.logout_button)) {
            logout();
        } else if (view == findViewById(R.id.unlink_button)) {
            unlink();
        } else if (view == findViewById(R.id.tokens_button)) {
            getPushTokens();
        }

    }

    /**
     * 푸시 서비스 사용중 세션이 닫히면, 즉 다시 로그인이 필요한 상태이면 호출되는 메쏘드로 로그인 페이지로 전환해준다.
     */
    @Override
    protected void redirectLoginActivity() {
        final Intent intent = new Intent(this, PushLoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void logout() {
        deregisterPushToken();
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            protected void onSuccess(final long userId) {
                redirectLoginActivity();
            }

            @Override
            protected void onFailure(final APIErrorResult apiErrorResult) {
                redirectLoginActivity();
            }
        });
    }

    private void unlink() {
        deregisterPushTokenAll();
        UserManagement.requestUnlink(new UnlinkResponseCallback() {
            @Override
            protected void onSuccess(final long userId) {
                redirectLoginActivity();
            }

            @Override
            protected void onSessionClosedFailure(final APIErrorResult errorResult) {
                redirectLoginActivity();
            }

            @Override
            protected void onFailure(final APIErrorResult errorResult) {
                Logger.getInstance().d("failure to unlink. msg = " + errorResult);
                redirectLoginActivity();
            }
        });

    }

    private void deregisterPushTokenAll() {
        PushService.deregisterPushTokenAll(new PushDeregisterHttpResponseHandler() {
            @Override
            protected void onHttpSuccess(Void resultObj) {
                super.onHttpSuccess(resultObj);
                Toast.makeText(getApplicationContext(), "succeeded to deregister all push token of this user", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onHttpSessionClosedFailure(APIErrorResult errorResult) {
                redirectLoginActivity();
            }

            @Override
            protected void onHttpFailure(APIErrorResult errorResult) {
                super.onHttpFailure(errorResult);
                Toast.makeText(getApplicationContext(), errorResult.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deregisterPushToken() {
        PushService.deregisterPushToken(new PushDeregisterHttpResponseHandler() {
            @Override
            protected void onHttpSuccess(Void resultObj) {
                super.onHttpSuccess(resultObj);
                Toast.makeText(getApplicationContext(), "succeeded to deregister push token", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onHttpSessionClosedFailure(APIErrorResult errorResult) {
                redirectLoginActivity();
            }

            @Override
            protected void onHttpFailure(APIErrorResult errorResult) {
                super.onHttpFailure(errorResult);
                Toast.makeText(getApplicationContext(), errorResult.toString(), Toast.LENGTH_LONG).show();
            }
        }, deviceUUID);
    }

    private void sendPushMessageToMe() {
        final String testMessage = new PushMessageBuilder("{\"content\":\"테스트 메시지\", \"friend_id\":1, \"noti\":\"test\"}").toString();
        if (testMessage == null) {
            Logger.getInstance().w("failed to create push Message");
        } else {
            PushService.sendPushMessage(new PushSendHttpResponseHandler() {
                @Override
                protected void onHttpSuccess(Void resultObj) {
                    Toast.makeText(getApplicationContext(), "succeeded to send message", Toast.LENGTH_SHORT).show();
                }

                @Override
                protected void onHttpSessionClosedFailure(APIErrorResult errorResult) {
                    redirectLoginActivity();
                }

                @Override
                protected void onHttpFailure(APIErrorResult errorResult) {
                    Toast.makeText(getApplicationContext(), errorResult.toString(), Toast.LENGTH_LONG).show();
                }
            }, testMessage, deviceUUID);
        }
    }

    private void getPushTokens() {
            PushService.getPushTokens(new PushTokensHttpResponseHandler<PushTokenInfo[]>() {
                @Override
                protected void onHttpSuccess(PushTokenInfo[] pushTokenInfos) {
                    Toast.makeText(getApplicationContext(), "succeeded to get push tokens." +
                        "\ncount=" + pushTokenInfos.length +
                        "\nstories=" + Arrays.toString(pushTokenInfos), Toast.LENGTH_SHORT).show();
                }

                @Override
                protected void onHttpSessionClosedFailure(APIErrorResult errorResult) {
                    redirectLoginActivity();
                }

                @Override
                protected void onHttpFailure(APIErrorResult errorResult) {
                    Toast.makeText(getApplicationContext(), errorResult.toString(), Toast.LENGTH_LONG).show();
                }
            });
    }

}
