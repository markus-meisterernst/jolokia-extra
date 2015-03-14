/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jolokia.extra.addon.scripting.model;

import org.jolokia.extra.addon.scripting.exception.ValidationException;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;

import java.util.*;

/**
 * List implementation to maintain and validate Assertions.
 *
 * @see org.jolokia.extra.addon.scripting.model.HealthCheckScript
 */
public class Assertions extends ScriptingModelItem implements List<Assertion>, ModelValidation {
    private List<Assertion> assertions = new ArrayList<Assertion>();

    public Assertions() {}

    public Assertions(List<Assertion> assertions) {
        if(assertions != null) {
            this.assertions.addAll(assertions);
        }
    }

    public int size() {
        return assertions.size();
    }

    public boolean isEmpty() {
        return assertions.isEmpty();
    }

    public boolean contains(Object o) {
        return assertions.contains(o);
    }

    public Iterator<Assertion> iterator() {
        return assertions.iterator();
    }

    public Object[] toArray() {
        return assertions.toArray();
    }

    public <T> T[] toArray(T[] ts) {
        return assertions.toArray(ts);
    }

    public boolean add(Assertion assertion) {
        return assertions.add(assertion);
    }

    public boolean remove(Object o) {
        return assertions.remove(o);
    }

    public boolean containsAll(Collection<?> collection) {
        return assertions.containsAll(collection);
    }

    public boolean addAll(Collection<? extends Assertion> collection) {
        return assertions.addAll(collection);
    }

    public boolean addAll(int i, Collection<? extends Assertion> collection) {
        return assertions.addAll(i, collection);
    }

    public boolean removeAll(Collection<?> collection) {
        return assertions.removeAll(collection);
    }

    public boolean retainAll(Collection<?> collection) {
        return assertions.retainAll(collection);
    }

    public void clear() {
        assertions.clear();
    }

    @Override
    public boolean equals(Object o) {
        return assertions.equals(o);
    }

    @Override
    public int hashCode() {
        return assertions.hashCode();
    }

    @Override
    public String toString() {
        return Arrays.deepToString(assertions.toArray());
    }

    public Assertion get(int i) {
        return assertions.get(i);
    }

    public Assertion set(int i, Assertion assertion) {
        return assertions.set(i, assertion);
    }

    public void add(int i, Assertion assertion) {
        assertions.add(i, assertion);
    }

    public Assertion remove(int i) {
        return assertions.remove(i);
    }

    public int indexOf(Object o) {
        return assertions.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return assertions.lastIndexOf(o);
    }

    public ListIterator<Assertion> listIterator() {
        return assertions.listIterator();
    }

    public ListIterator<Assertion> listIterator(int i) {
        return assertions.listIterator(i);
    }

    public List<Assertion> subList(int i, int i1) {
        return assertions.subList(i, i1);
    }

    public void validate() throws ValidationException {
        for (Assertion assertion : assertions) {
            assertion.validate();
        }
    }

    @Override
    public JSONAware toJSON() {
        JSONArray array = new JSONArray();
        for(Assertion assertion: assertions) {
            array.add(assertion.toJSON());
        }
        return array;
    }
}

