/**
 * Copyright (c) 2024 Nabil Andriantomanga
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.bwi4j.implementation;

import org.bwi4j.PackageScanner;
import org.bwi4j.TermChecker;
import org.bwi4j.annotation.BwiIgnore;
import org.bwi4j.exception.BwiException;
import org.bwi4j.model.TermType;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Nabil Andriantomanga
 * @version 1.0
 * @since 2024
 */
public class DefaultPackageScanner implements PackageScanner {
    private static final Logger LOGGER = Logger.getLogger(DefaultPackageScanner.class.getName());
    private final TermChecker termChecker;

    public DefaultPackageScanner(TermChecker termChecker) {
        this.termChecker = termChecker;
    }

    @Override
    public void checkClass(Class<?> clazz) throws BwiException {
        termChecker.checkName(TermType.CLASS, clazz.getSimpleName());
    }

    @Override
    public void checkMethods(Class<?> clazz) throws BwiException {
        for (var method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(BwiIgnore.class)) {
                LOGGER.log(Level.INFO, "Ignoring the following method: {0}", method.getName());
                continue;
            }
            termChecker.checkName(TermType.METHOD, method.getName());
        }
    }

    @Override
    public void checkFields(Class<?> clazz) throws BwiException {
        for (var field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(BwiIgnore.class)) {
                LOGGER.log(Level.INFO, "Ignoring the following field: {0}", field.getName());
                continue;
            }
            termChecker.checkName(TermType.FIELD, field.getName());
        }
    }
}
