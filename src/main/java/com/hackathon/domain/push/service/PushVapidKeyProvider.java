package com.hackathon.domain.push.service;

import com.hackathon.domain.push.config.PushVapidProperties;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Base64Encoder;
import nl.martijndwars.webpush.Utils;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;

import static nl.martijndwars.webpush.Utils.ALGORITHM;
import static nl.martijndwars.webpush.Utils.CURVE;
import static org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME;

@Slf4j
@Component
public class PushVapidKeyProvider {

	private static final String DEFAULT_SUBJECT = "mailto:dev-push@example.com";

	private final PushVapidKeys pushVapidKeys;

	public PushVapidKeyProvider(PushVapidProperties pushVapidProperties) {
		this.pushVapidKeys = pushVapidProperties.isConfigured()
				? new PushVapidKeys(
						pushVapidProperties.publicKey(),
						pushVapidProperties.privateKey(),
						pushVapidProperties.subject()
				)
				: generateDevelopmentKeys();
	}

	public String getPublicKey() {
		return pushVapidKeys.publicKey();
	}

	public String getPrivateKey() {
		return pushVapidKeys.privateKey();
	}

	public String getSubject() {
		return pushVapidKeys.subject();
	}

	private PushVapidKeys generateDevelopmentKeys() {
		try {
			if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
				Security.addProvider(new BouncyCastleProvider());
			}

			ECNamedCurveParameterSpec parameterSpec = ECNamedCurveTable.getParameterSpec(CURVE);
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER_NAME);
			keyPairGenerator.initialize(parameterSpec);

			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			String publicKey = Base64Encoder.encodeUrlWithoutPadding(
					Utils.encode((ECPublicKey) keyPair.getPublic())
			);
			String privateKey = Base64Encoder.encodeUrlWithoutPadding(
					Utils.encode((ECPrivateKey) keyPair.getPrivate())
			);

			log.warn("VAPID environment variables are missing. Generated temporary development VAPID keys.");
			return new PushVapidKeys(publicKey, privateKey, DEFAULT_SUBJECT);
		} catch (Exception exception) {
			throw new IllegalStateException("임시 VAPID 키 생성에 실패했습니다.", exception);
		}
	}

	private record PushVapidKeys(
			String publicKey,
			String privateKey,
			String subject
	) {}
}
