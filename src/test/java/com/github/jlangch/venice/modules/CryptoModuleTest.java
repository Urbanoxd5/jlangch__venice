/*   __    __         _
 *   \ \  / /__ _ __ (_) ___ ___
 *    \ \/ / _ \ '_ \| |/ __/ _ \
 *     \  /  __/ | | | | (_|  __/
 *      \/ \___|_| |_|_|\___\___|
 *
 *
 * Copyright 2017-2022 Venice
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
package com.github.jlangch.venice.modules;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.github.jlangch.venice.Venice;


public class CryptoModuleTest {

    @Test
    public void test_PBKDEF2_1() {
        final Venice venice = new Venice();

        final String script =
                "(do                                                \n" +
                "  (load-module :crypt)                             \n" +
                "  (str/bytebuf-to-hex                              \n" +
                "    (crypt/pbkdf2-hash \"hello world\" \"-salt-\") \n" +
                "    :upper))                                         ";

        assertEquals(
            "54F2B4411E8817C2A0743B2A7DD7EAE5AA3F748D1DDDCE00766380914AFFE995",
            venice.eval(script));
    }

    @Test
    public void test_PBKDEF2_2() {
        final Venice venice = new Venice();

        final String script =
                "(do                                                  \n" +
                "  (load-module :crypt)                               \n" +
                "  (str/bytebuf-to-hex                                \n" +
                "    (crypt/pbkdf2-hash \"hello world\" \"-salt-\")))   ";

        assertEquals(
            "54f2b4411e8817c2a0743b2a7dd7eae5aa3f748d1dddce00766380914affe995",
            venice.eval(script));
    }

    @Test
    public void test_PBKDEF2_3() {
        final Venice venice = new Venice();

        final String script =
                "(do                                                         \n" +
                "  (load-module :crypt)                                      \n" +
                "  (str/bytebuf-to-hex                                       \n" +
                "    (crypt/pbkdf2-hash \"hello world\" \"-salt-\" 1000 256) \n" +
                "    :upper))                                                 ";

        assertEquals(
            "54F2B4411E8817C2A0743B2A7DD7EAE5AA3F748D1DDDCE00766380914AFFE995",
            venice.eval(script));
    }

    @Test
    public void test_PBKDEF2_4() {
        final Venice venice = new Venice();

        final String script =
                "(do                                                         \n" +
                "  (load-module :crypt)                                      \n" +
                "  (str/bytebuf-to-hex                                       \n" +
                "    (crypt/pbkdf2-hash \"hello world\" \"-salt-\" 1000 384) \n" +
                "    :upper))                                                 ";

        assertEquals(
            "54F2B4411E8817C2A0743B2A7DD7EAE5AA3F748D1DDDCE00766380914AFFE995281C8170AE437EC63B13BB50FD1A480E",
            venice.eval(script));
    }

    @Test
    public void test_SHA_512_1() {
        final Venice venice = new Venice();

        final String script =
                "(do                                                \n" +
                "  (load-module :crypt)                             \n" +
                "  (str/bytebuf-to-hex                              \n" +
                "    (crypt/sha512-hash \"hello world\" \"-salt-\") \n" +
                "    :upper))                                         ";

        assertEquals(
            "316EBB70239D9480E91089D5D5BC6428879DF6E5CFB651B39D7AFC27DFF259418105C6D78F307FC6197531FBD37C4E8103095F186B19FC33C93D60282F3314A2",
            venice.eval(script));
    }

    @Test
    public void test_SHA_512_2() {
        final Venice venice = new Venice();

        final String script =
                "(do                                                  \n" +
                "  (load-module :crypt)                               \n" +
                "  (str/bytebuf-to-hex                                \n" +
                "    (crypt/sha512-hash \"hello world\" \"-salt-\")))   ";

        assertEquals(
            "316ebb70239d9480e91089d5d5bc6428879df6e5cfb651b39d7afc27dff259418105c6d78f307fc6197531fbd37c4e8103095f186b19fc33c93d60282f3314a2",
            venice.eval(script));
    }

    @Test
    public void test_SHA_512_3() {
        final Venice venice = new Venice();

        final String script =
                "(do                                                     \n" +
                "  (load-module :crypt)                                  \n" +
                "  (str/bytebuf-to-hex                                   \n" +
                "    (crypt/sha512-hash (bytebuf [54 78 99]) \"-salt-\") \n" +
                "    :upper))                                              ";

        assertEquals(
            "02621CADC0EA2E051EFCBE77A5BDEDC6AC77ECA0A06D97801485A9AC2BC9DFBE08D0671FE03D6B249F954C890FF812D2FA345FE6B8BF54DB3D2DCD3EDE3B9351",
            venice.eval(script));
    }

    @Test
    public void test_MD5_1() {
        final Venice venice = new Venice();

        final String script =
                "(do                                                \n" +
                "  (load-module :crypt)                             \n" +
                "  (str/bytebuf-to-hex                              \n" +
                "    (crypt/md5-hash \"hello world\")               \n" +
                "    :upper))                                         ";

        assertEquals(
            "5EB63BBBE01EEED093CB22BB8F5ACDC3",
            venice.eval(script));
    }

    @Test
    public void test_MD5_2() {
        final Venice venice = new Venice();

        final String script =
                "(do                                                \n" +
                "  (load-module :crypt)                             \n" +
                "  (str/bytebuf-to-hex                              \n" +
                "    (crypt/md5-hash \"hello world\")))               ";

        assertEquals(
            "5eb63bbbe01eeed093cb22bb8f5acdc3",
            venice.eval(script));
    }

    @Test
    public void test_MD5_3() {
        final Venice venice = new Venice();

        final String script =
                "(do                                                  \n" +
                "  (load-module :crypt)                               \n" +
                "  (str/bytebuf-to-hex                                \n" +
                "    (crypt/md5-hash (bytebuf [54 78 99]))            \n" +
                "    :upper))                                           ";

        assertEquals(
            "7F83326444205E182B3E80D1C65C902D",
            venice.eval(script));
    }

    @Test
    public void test_DES_encrypt_1() {
        final Venice venice = new Venice();

        final String script =
                "(do                                                                             \n" +
                "  (load-module :crypt)                                                          \n" +
                "  (def encrypt (crypt/encrypt \"DES\" \"secret\" :url-safe true))               \n" +
                "  (assert (== \"QdxpapAEjgI\" (encrypt \"hello\")))                             \n" +
                ")";

        venice.eval(script);
    }

    @Test
    public void test_DES_encrypt_2() {
        final Venice venice = new Venice();

        final String script =
                "(do                                                                             \n" +
                "  (load-module :crypt)                                                          \n" +
                "  (def encrypt (crypt/encrypt \"DES\" \"secret\" :url-safe true))               \n" +
                "  (def decrypt (crypt/decrypt \"DES\" \"secret\" :url-safe true))               \n" +
                "  (assert (== \"hello\" (decrypt (encrypt \"hello\"))))                         \n" +
                "  (assert (== (bytebuf [1 2 3 4 5]) (decrypt (encrypt (bytebuf [1 2 3 4 5]))))) \n" +
                ")";

        venice.eval(script);
    }

    @Test
    public void test_3DES_encrypt_1() {
        final Venice venice = new Venice();

        final String script =
                "(do                                                                             \n" +
                "  (load-module :crypt)                                                          \n" +
                "  (def encrypt (crypt/encrypt \"3DES\" \"secret\" :url-safe true))              \n" +
                "  (assert (== \"ndmW1NLsDHA\" (encrypt \"hello\")))                             \n" +
                ")";

        venice.eval(script);
    }

    @Test
    public void test_3DES_encrypt_2() {
        final Venice venice = new Venice();

        final String script =
                "(do                                                                             \n" +
                "  (load-module :crypt)                                                          \n" +
                "  (def encrypt (crypt/encrypt \"3DES\" \"secret\" :url-safe true))              \n" +
                "  (def decrypt (crypt/decrypt \"3DES\" \"secret\" :url-safe true))              \n" +
                "  (assert (== \"hello\" (decrypt (encrypt \"hello\"))))                         \n" +
                "  (assert (== (bytebuf [1 2 3 4 5]) (decrypt (encrypt (bytebuf [1 2 3 4 5]))))) \n" +
                ")";

        venice.eval(script);
    }

    @Test
    public void test_AES256_encrypt_1() {
        final Venice venice = new Venice();

        final String script =
                "(do                                                                             \n" +
                "  (load-module :crypt)                                                          \n" +
                "  (def encrypt (crypt/encrypt \"AES256\" \"secret\" :url-safe true))            \n" +
                "  (assert (== \"e4m1qe6Fyx3Rr7NTIZe97g\" (encrypt \"hello\")))                  \n" +
                ")";

        venice.eval(script);
    }

    @Test
    public void test_AES256_encrypt_2() {
        final Venice venice = new Venice();

        final String script =
                "(do                                                                             \n" +
                "  (load-module :crypt)                                                          \n" +
                "  (def encrypt (crypt/encrypt \"AES256\" \"secret\" :url-safe true))            \n" +
                "  (def decrypt (crypt/decrypt \"AES256\" \"secret\" :url-safe true))            \n" +
                "  (assert (== \"hello\" (decrypt (encrypt \"hello\"))))                          \n" +
                "  (assert (== (bytebuf [1 2 3 4 5]) (decrypt (encrypt (bytebuf [1 2 3 4 5]))))) \n" +
                ")";

        venice.eval(script);
    }
}



