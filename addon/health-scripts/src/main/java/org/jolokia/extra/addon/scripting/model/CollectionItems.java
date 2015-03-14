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

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;

import java.util.*;

/**
 * List implementation to maintain and validate CollectionItems.
 *
 * @see org.jolokia.extra.addon.scripting.model.DataCollectionScript
 * @see org.jolokia.extra.addon.scripting.model.Conditional
 */
public class CollectionItems extends ScriptingModelItem implements List<CollectionItem>, ModelValidation {
    private List<CollectionItem> collectionItems = new ArrayList<CollectionItem>();

    public CollectionItems() {}

    public CollectionItems(List<CollectionItem> collectionItems) {
        if(collectionItems != null) {
            this.collectionItems.addAll(collectionItems);
        }
    }

    public int size() {
        return collectionItems.size();
    }

    public boolean isEmpty() {
        return collectionItems.isEmpty();
    }

    public boolean contains(Object o) {
        return collectionItems.contains(o);
    }

    public Iterator<CollectionItem> iterator() {
        return collectionItems.iterator();
    }

    public Object[] toArray() {
        return collectionItems.toArray();
    }

    public <T> T[] toArray(T[] ts) {
        return collectionItems.toArray(ts);
    }

    public boolean add(CollectionItem collectionItem) {
        return collectionItems.add(collectionItem);
    }

    public boolean remove(Object o) {
        return collectionItems.remove(o);
    }

    public boolean containsAll(Collection<?> collection) {
        return collectionItems.containsAll(collection);
    }

    public boolean addAll(Collection<? extends CollectionItem> collection) {
        return collectionItems.addAll(collection);
    }

    public boolean addAll(int i, Collection<? extends CollectionItem> collection) {
        return collectionItems.addAll(i, collection);
    }

    public boolean removeAll(Collection<?> collection) {
        return collectionItems.removeAll(collection);
    }

    public boolean retainAll(Collection<?> collection) {
        return collectionItems.retainAll(collection);
    }

    public void clear() {
        collectionItems.clear();
    }

    @Override
    public boolean equals(Object o) {
        return collectionItems.equals(o);
    }

    @Override
    public int hashCode() {
        return collectionItems.hashCode();
    }

    @Override
    public String toString() {
        return Arrays.deepToString(collectionItems.toArray());
    }

    public CollectionItem get(int i) {
        return collectionItems.get(i);
    }

    public CollectionItem set(int i, CollectionItem collectionItem) {
        return collectionItems.set(i, collectionItem);
    }

    public void add(int i, CollectionItem collectionItem) {
        collectionItems.add(i, collectionItem);
    }

    public CollectionItem remove(int i) {
        return collectionItems.remove(i);
    }

    public int indexOf(Object o) {
        return collectionItems.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return collectionItems.lastIndexOf(o);
    }

    public ListIterator<CollectionItem> listIterator() {
        return collectionItems.listIterator();
    }

    public ListIterator<CollectionItem> listIterator(int i) {
        return collectionItems.listIterator(i);
    }

    public List<CollectionItem> subList(int i, int i1) {
        return collectionItems.subList(i, i1);
    }

    public void validate() {
        for (CollectionItem item : collectionItems) {
            item.validate();
        }
    }

    @Override
    public JSONAware toJSON() {
        JSONArray jsonArray = new JSONArray();
        for(CollectionItem collectionItem : collectionItems) {
            jsonArray.add(collectionItem.toJSON());
        }
        return jsonArray;
    }
}
