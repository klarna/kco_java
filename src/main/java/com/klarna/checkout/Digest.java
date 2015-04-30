/*
 * Copyright 2015 Klarna AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.klarna.checkout;

import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to handle the digesting of hash string.
 */
public class Digest {

    /**
     * Shared secret holder.
     */
    private final String secret;

    /**
     * MessageDigest object.
     */
    private MessageDigest md;

    /**
     * Constructor.
     *
     * @param sharedSecret Shared Secret
     * @throws NoSuchAlgorithmException if SHA-256 is not supported by
     *                                  the Java VM.
     */
    public Digest(final String sharedSecret) throws NoSuchAlgorithmException {
        this.secret = sharedSecret;
        this.md = MessageDigest.getInstance("SHA-256");
    }

    /**
     * Create a digest from a supplied string.
     *
     * @param message string to hash
     * @return Base64 and SHA256 hashed string
     * @throws UnsupportedEncodingException if UTF-8 is unsupported
     */
    public String create(final String message)
            throws UnsupportedEncodingException {
        md.reset();

        if (message != null) {
            md.update(message.getBytes("UTF-8"));
        }

        md.update(secret.getBytes("UTF-8"));

        return new String(Base64.encodeBase64(md.digest()));
    }

    /**
     * Create a digest from an input stream.
     *
     * @param stream character stream to hash
     * @return Base64 and SHA256 hashed string
     * @throws UnsupportedEncodingException if UTF-8 is unsupported
     */
    public String create(final InputStream stream)
            throws UnsupportedEncodingException {
        md.reset();

        if (stream != null) {

            byte[] b = new byte[1024];
            int read;

            try {
                while ((read = stream.read(b)) >= 0) {
                    md.update(b, 0, read);
                }
            } catch (IOException ex) {
                Logger.getLogger(
                        Digest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        md.update(secret.getBytes("UTF-8"));

        return new String(Base64.encodeBase64(md.digest()));
    }
}
