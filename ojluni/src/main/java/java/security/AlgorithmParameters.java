/*
 * Copyright (c) 1997, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package java.security;

import java.io.*;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;

/**
 * This class is used as an opaque representation of cryptographic parameters.
 *
 * <p>An {@code AlgorithmParameters} object for managing the parameters
 * for a particular algorithm can be obtained by
 * calling one of the {@code getInstance} factory methods
 * (static methods that return instances of a given class).
 *
 * <p>Once an {@code AlgorithmParameters} object is obtained, it must be
 * initialized via a call to {@code init}, using an appropriate parameter
 * specification or parameter encoding.
 *
 * <p>A transparent parameter specification is obtained from an
 * {@code AlgorithmParameters} object via a call to
 * {@code getParameterSpec}, and a byte encoding of the parameters is
 * obtained via a call to {@code getEncoded}.
 *
 * <p> Android provides the following <code>AlgorithmParameters</code> algorithms:
 * <table>
 *     <thead>
 *         <tr>
 *             <th>Name</th>
 *             <th>Supported (API Levels)</th>
 *         </tr>
 *     </thead>
 *     <tbody>
 *         <tr>
 *             <td>AES</td>
 *             <td>1+</td>
 *         </tr>
 *         <tr>
 *             <td>Blowfish</td>
 *             <td>10+</td>
 *         </tr>
 *         <tr>
 *             <td>DES</td>
 *             <td>1+</td>
 *         </tr>
 *         <tr>
 *             <td>DESede</td>
 *             <td>1+</td>
 *         </tr>
 *         <tr>
 *             <td>DH</td>
 *             <td>1+</td>
 *         </tr>
 *         <tr>
 *             <td>DSA</td>
 *             <td>1+</td>
 *         </tr>
 *         <tr>
 *             <td>GCM</td>
 *             <td>22+</td>
 *         </tr>
 *         <tr>
 *             <td>IES</td>
 *             <td>1&ndash;8</td>
 *         </tr>
 *         <tr>
 *             <td>OAEP</td>
 *             <td>1+</td>
 *         </tr>
 *         <tr>
 *             <td>PKCS12PBE</td>
 *             <td>1+</td>
 *         </tr>
 *         <tr>
 *             <td>PSS</td>
 *             <td>1&ndash;8, 24+</td>
 *         </tr>
 *     </tbody>
 * </table>
 *
 * These algorithms are described in the <a href=
 * "{@docRoot}openjdk-redirect.html?v=8&path=/technotes/guides/security/StandardNames.html#AlgorithmParameters">
 * AlgorithmParameters section</a> of the
 * Java Cryptography Architecture Standard Algorithm Name Documentation.
 *
 * @author Jan Luehe
 *
 *
 * @see java.security.spec.AlgorithmParameterSpec
 * @see java.security.spec.DSAParameterSpec
 * @see KeyPairGenerator
 *
 * @since 1.2
 */

public class AlgorithmParameters {

    // The provider
    private Provider provider;

    // The provider implementation (delegate)
    private AlgorithmParametersSpi paramSpi;

    // The algorithm
    private String algorithm;

    // Has this object been initialized?
    private boolean initialized = false;

    /**
     * Creates an AlgorithmParameters object.
     *
     * @param paramSpi the delegate
     * @param provider the provider
     * @param algorithm the algorithm
     */
    protected AlgorithmParameters(AlgorithmParametersSpi paramSpi,
                                  Provider provider, String algorithm)
    {
        this.paramSpi = paramSpi;
        this.provider = provider;
        this.algorithm = algorithm;
    }

    /**
     * Returns the name of the algorithm associated with this parameter object.
     *
     * @return the algorithm name.
     */
    public final String getAlgorithm() {
        return this.algorithm;
    }

    /**
     * Returns a parameter object for the specified algorithm.
     *
     * <p> This method traverses the list of registered security Providers,
     * starting with the most preferred Provider.
     * A new AlgorithmParameters object encapsulating the
     * AlgorithmParametersSpi implementation from the first
     * Provider that supports the specified algorithm is returned.
     *
     * <p> Note that the list of registered providers may be retrieved via
     * the {@link Security#getProviders() Security.getProviders()} method.
     *
     * <p> The returned parameter object must be initialized via a call to
     * {@code init}, using an appropriate parameter specification or
     * parameter encoding.
     *
     * @param algorithm the name of the algorithm requested.
     * See the AlgorithmParameters section in the <a href=
     * "{@docRoot}openjdk-redirect.html?v=8&path=/technotes/guides/security/StandardNames.html#AlgorithmParameters">
     * Java Cryptography Architecture Standard Algorithm Name Documentation</a>
     * for information about standard algorithm names.
     *
     * @return the new parameter object.
     *
     * @exception NoSuchAlgorithmException if no Provider supports an
     *          AlgorithmParametersSpi implementation for the
     *          specified algorithm.
     *
     * @see Provider
     */
    public static AlgorithmParameters getInstance(String algorithm)
    throws NoSuchAlgorithmException {
        try {
            Object[] objs = Security.getImpl(algorithm, "AlgorithmParameters",
                                             (String)null);
            return new AlgorithmParameters((AlgorithmParametersSpi)objs[0],
                                           (Provider)objs[1],
                                           algorithm);
        } catch(NoSuchProviderException e) {
            throw new NoSuchAlgorithmException(algorithm + " not found");
        }
    }

    /**
     * Returns a parameter object for the specified algorithm.
     *
     * <p> A new AlgorithmParameters object encapsulating the
     * AlgorithmParametersSpi implementation from the specified provider
     * is returned.  The specified provider must be registered
     * in the security provider list.
     *
     * <p> Note that the list of registered providers may be retrieved via
     * the {@link Security#getProviders() Security.getProviders()} method.
     *
     * <p>The returned parameter object must be initialized via a call to
     * {@code init}, using an appropriate parameter specification or
     * parameter encoding.
     *
     * @param algorithm the name of the algorithm requested.
     * See the AlgorithmParameters section in the <a href=
     * "{@docRoot}openjdk-redirect.html?v=8&path=/technotes/guides/security/StandardNames.html#AlgorithmParameters">
     * Java Cryptography Architecture Standard Algorithm Name Documentation</a>
     * for information about standard algorithm names.
     *
     * @param provider the name of the provider.
     *
     * @return the new parameter object.
     *
     * @exception NoSuchAlgorithmException if an AlgorithmParametersSpi
     *          implementation for the specified algorithm is not
     *          available from the specified provider.
     *
     * @exception NoSuchProviderException if the specified provider is not
     *          registered in the security provider list.
     *
     * @exception IllegalArgumentException if the provider name is null
     *          or empty.
     *
     * @see Provider
     */
    public static AlgorithmParameters getInstance(String algorithm,
                                                  String provider)
        throws NoSuchAlgorithmException, NoSuchProviderException
    {
        if (provider == null || provider.length() == 0)
            throw new IllegalArgumentException("missing provider");
        Object[] objs = Security.getImpl(algorithm, "AlgorithmParameters",
                                         provider);
        return new AlgorithmParameters((AlgorithmParametersSpi)objs[0],
                                       (Provider)objs[1],
                                       algorithm);
    }

    /**
     * Returns a parameter object for the specified algorithm.
     *
     * <p> A new AlgorithmParameters object encapsulating the
     * AlgorithmParametersSpi implementation from the specified Provider
     * object is returned.  Note that the specified Provider object
     * does not have to be registered in the provider list.
     *
     * <p>The returned parameter object must be initialized via a call to
     * {@code init}, using an appropriate parameter specification or
     * parameter encoding.
     *
     * @param algorithm the name of the algorithm requested.
     * See the AlgorithmParameters section in the <a href=
     * "{@docRoot}openjdk-redirect.html?v=8&path=/technotes/guides/security/StandardNames.html#AlgorithmParameters">
     * Java Cryptography Architecture Standard Algorithm Name Documentation</a>
     * for information about standard algorithm names.
     *
     * @param provider the name of the provider.
     *
     * @return the new parameter object.
     *
     * @exception NoSuchAlgorithmException if an AlgorithmParameterGeneratorSpi
     *          implementation for the specified algorithm is not available
     *          from the specified Provider object.
     *
     * @exception IllegalArgumentException if the provider is null.
     *
     * @see Provider
     *
     * @since 1.4
     */
    public static AlgorithmParameters getInstance(String algorithm,
                                                  Provider provider)
        throws NoSuchAlgorithmException
    {
        if (provider == null)
            throw new IllegalArgumentException("missing provider");
        Object[] objs = Security.getImpl(algorithm, "AlgorithmParameters",
                                         provider);
        return new AlgorithmParameters((AlgorithmParametersSpi)objs[0],
                                       (Provider)objs[1],
                                       algorithm);
    }

    /**
     * Returns the provider of this parameter object.
     *
     * @return the provider of this parameter object
     */
    public final Provider getProvider() {
        return this.provider;
    }

    /**
     * Initializes this parameter object using the parameters
     * specified in {@code paramSpec}.
     *
     * @param paramSpec the parameter specification.
     *
     * @exception InvalidParameterSpecException if the given parameter
     * specification is inappropriate for the initialization of this parameter
     * object, or if this parameter object has already been initialized.
     */
    public final void init(AlgorithmParameterSpec paramSpec)
        throws InvalidParameterSpecException
    {
        if (this.initialized)
            throw new InvalidParameterSpecException("already initialized");
        paramSpi.engineInit(paramSpec);
        this.initialized = true;
    }

    /**
     * Imports the specified parameters and decodes them according to the
     * primary decoding format for parameters. The primary decoding
     * format for parameters is ASN.1, if an ASN.1 specification for this type
     * of parameters exists.
     *
     * @param params the encoded parameters.
     *
     * @exception IOException on decoding errors, or if this parameter object
     * has already been initialized.
     */
    public final void init(byte[] params) throws IOException {
        if (this.initialized)
            throw new IOException("already initialized");
        paramSpi.engineInit(params);
        this.initialized = true;
    }

    /**
     * Imports the parameters from {@code params} and decodes them
     * according to the specified decoding scheme.
     * If {@code format} is null, the
     * primary decoding format for parameters is used. The primary decoding
     * format is ASN.1, if an ASN.1 specification for these parameters
     * exists.
     *
     * @param params the encoded parameters.
     *
     * @param format the name of the decoding scheme.
     *
     * @exception IOException on decoding errors, or if this parameter object
     * has already been initialized.
     */
    public final void init(byte[] params, String format) throws IOException {
        if (this.initialized)
            throw new IOException("already initialized");
        paramSpi.engineInit(params, format);
        this.initialized = true;
    }

    /**
     * Returns a (transparent) specification of this parameter object.
     * {@code paramSpec} identifies the specification class in which
     * the parameters should be returned. It could, for example, be
     * {@code DSAParameterSpec.class}, to indicate that the
     * parameters should be returned in an instance of the
     * {@code DSAParameterSpec} class.
     *
     * @param <T> the type of the parameter specification to be returrned
     * @param paramSpec the specification class in which
     * the parameters should be returned.
     *
     * @return the parameter specification.
     *
     * @exception InvalidParameterSpecException if the requested parameter
     * specification is inappropriate for this parameter object, or if this
     * parameter object has not been initialized.
     */
    public final <T extends AlgorithmParameterSpec>
        T getParameterSpec(Class<T> paramSpec)
        throws InvalidParameterSpecException
    {
        if (this.initialized == false) {
            throw new InvalidParameterSpecException("not initialized");
        }
        return paramSpi.engineGetParameterSpec(paramSpec);
    }

    /**
     * Returns the parameters in their primary encoding format.
     * The primary encoding format for parameters is ASN.1, if an ASN.1
     * specification for this type of parameters exists.
     *
     * @return the parameters encoded using their primary encoding format.
     *
     * @exception IOException on encoding errors, or if this parameter object
     * has not been initialized.
     */
    public final byte[] getEncoded() throws IOException
    {
        if (this.initialized == false) {
            throw new IOException("not initialized");
        }
        return paramSpi.engineGetEncoded();
    }

    /**
     * Returns the parameters encoded in the specified scheme.
     * If {@code format} is null, the
     * primary encoding format for parameters is used. The primary encoding
     * format is ASN.1, if an ASN.1 specification for these parameters
     * exists.
     *
     * @param format the name of the encoding format.
     *
     * @return the parameters encoded using the specified encoding scheme.
     *
     * @exception IOException on encoding errors, or if this parameter object
     * has not been initialized.
     */
    public final byte[] getEncoded(String format) throws IOException
    {
        if (this.initialized == false) {
            throw new IOException("not initialized");
        }
        return paramSpi.engineGetEncoded(format);
    }

    /**
     * Returns a formatted string describing the parameters.
     *
     * @return a formatted string describing the parameters, or null if this
     * parameter object has not been initialized.
     */
    public final String toString() {
        if (this.initialized == false) {
            return null;
        }
        return paramSpi.engineToString();
    }
}
