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

import org.bwi4j.TermChecker;
import org.bwi4j.exception.BwiException;
import org.bwi4j.model.TermType;

import java.util.Set;

/**
 * @author Nabil Andriantomanga
 * @version 1.0
 * @since 2024
 */
public class DefaultTermChecker implements TermChecker {
    private final Set<String> blacklistedTerms;

    public DefaultTermChecker(Set<String> blacklistedTerms) {
        this.blacklistedTerms = blacklistedTerms;
    }

    @Override
    public void checkName(TermType type, String name) throws BwiException {
        for (var term : blacklistedTerms) {
            if (name.toLowerCase().contains(term)) {
                throw new BwiException(type.getName() + " name contains blacklisted term: " + name);
            }
        }
    }
}
