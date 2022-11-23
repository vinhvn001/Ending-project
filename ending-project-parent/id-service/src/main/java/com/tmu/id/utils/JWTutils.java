/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tmu.id.utils;

/**
 *
 * @author Admin
 */

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class JWTutils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JWTutils.class);
    
    private final static String SECRET_KEY = "elcom_wq3Dr8O5wrkCSybDkQ==1_2022@)@)";
    private static final long REFRESH_JWT_EXPIRATION = 2592000000L;//30day

    public static String createToken(String content) {
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + REFRESH_JWT_EXPIRATION);
            return JWT.create()
                    .withIssuer("auth0")
                    .withClaim("content", content)
                    .withExpiresAt(expiryDate)
                    .sign(Algorithm.HMAC256(SECRET_KEY));
        } catch (Exception ex) {
            LOGGER.info(ex.toString());
        }
        return null;
    }

    public static String createToken(String iss, String sub, String aud, String claimName, String claimValue, String secretKey, Date exp) {
        try {
            return JWT.create()
                    .withIssuer(iss) // iss
                    .withExpiresAt(exp) // exp
                    .withSubject(sub) // sub
                    .withAudience(aud) //aud
                    .withClaim(claimName, claimValue) //room	
                    .sign(Algorithm.HMAC256(secretKey));
        } catch (Exception ex) {
            LOGGER.info(ex.toString());
        }
        return null;
    }

    public static String getContentInToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET_KEY))
                    .withIssuer("auth0")
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("content").asString();
        } catch (Exception ex) {
            LOGGER.info(ex.toString());
        }
        return null;
    }

//    public static void main(String[] args) {
//        Date today = new Date();
//        Date tomorrow = new Date(today.getTime() + (1000 * 60 * 60 * 24 * 15));
        //LOGGER.info( createToken("meet.elcomvideo.cf", "meet.elcomvideo.cf", "*", "room", "*", "Elcom@123", tomorrow) );
        //LOGGER.info( JWTutils.createToken(InterviewConstant.JITSI_AUTH_ISS, InterviewConstant.JITSI_AUTH_SUB,
        //						                InterviewConstant.JITSI_AUTH_AUD, InterviewConstant.JITSI_AUTH_CLAIM_NAME,
        //						                InterviewConstant.JITSI_AUTH_CLAIM_VALUE, InterviewConstant.JITSI_AUTH_HS256_SECRET_KEY, tomorrow) );
        //LOGGER.info( getContentInToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhdXRoMCIsImNvbnRlbnQiOiJhbmhkdkBlbGNvbS5jb20udm4ifQ.l2cz86uK0zugYmTjwbIPry1LK4WzZP6ypazIb3JoCUM") );
        // eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhdXRoMCIsImNvbnRlbnQiOiJhbmhkdkBlbGNvbS5jb20udm4ifQ.YRJUCqtiZj8yaIENa8fsEwYlmf7uASK5eCE7XBZIRWk
//    }
}