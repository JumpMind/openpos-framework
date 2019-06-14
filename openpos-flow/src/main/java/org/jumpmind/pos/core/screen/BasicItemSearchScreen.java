package org.jumpmind.pos.core.screen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jumpmind.pos.core.model.Form;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BasicItemSearchScreen extends Screen {
    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(getClass());
    

    public enum ItemRetrievalMode {
        ALL,
        CALLBACK
    };
    
    public enum SearchCategoryStructure {
        FLAT,
        HIERARCHICAL
    };
    // For HIERARCHICAL list:
    // SearchCategoryStructure = HIERARCHICAL
    //  searchCategories = [ {ROOT,{name=top}}, {SUBCATEGORY,{name=level1}}, {SUBCATEGORY,{name=level2}} ]
    //  searchCategoryValues = [ {{name=top_item_1},[{{name=level1_item1},[]}]} ]
   
    // For FLAT list:
    // SearchCategoryStructure = FLAT
    //  searchCategories = [ {CATEGORY1,{name=cat1}}, {CATEGORY2,{name=cat2}}, {CATEGORY3,{name=cat3}} ]
    //  searchCategoryValues = [ {{name=cat1},[{name=item1},{name=item2},...]}, {{name=cat2},[{name=item1},{name=item2},...] ]
    
    public static final int MAX_SEARCH_CATEGORIES = 3;
    
    private ItemRetrievalMode itemRetrievalMode = ItemRetrievalMode.ALL;
    private SearchCategoryStructure searchCategoryStructure = SearchCategoryStructure.HIERARCHICAL;
    private String searchCategoriesText;
    private List<SearchCategory> searchCategories = new ArrayList<>();
    private List<SearchCategoryValue> searchCategoryValues = new ArrayList<>();
    private Form searchFieldForm;
    private SearchCategoryValue selectedCategoryValue;

    
    public BasicItemSearchScreen() {
        this.setScreenType(ScreenType.BasicItemSearch);
    }
    
    public void addSearchCategory(SearchCategory searchCategory) {
        this.searchCategories.add(searchCategory);
    }

    public void addSearchCategoryValue(SearchCategoryValue searchCategoryValue) {
        this.searchCategoryValues.add(searchCategoryValue);
    }
    
    public void clearSearchCategories() {
        this.searchCategories.clear();
    }
    
    public List<SearchCategory> getSearchCategories() {
        return Collections.unmodifiableList(this.searchCategories);
    }

    public void clearSearchCategoryValues() {
        this.searchCategoryValues.clear();
    }
    
    public List<SearchCategoryValue> getSearchCategoryValues() {
        return Collections.unmodifiableList(this.searchCategoryValues);
    }
    
    public ItemRetrievalMode getItemRetrievalMode() {
        return itemRetrievalMode;
    }

    public void setItemRetrievalMode(ItemRetrievalMode itemRetrievalMode) {
        this.itemRetrievalMode = itemRetrievalMode;
    }

    public SearchCategoryStructure getSearchCategoryStructure() {
        return searchCategoryStructure;
    }

    public void setSearchCategoryStructure(SearchCategoryStructure searchCategoryStructure) {
        this.searchCategoryStructure = searchCategoryStructure;
    }

    public String getSearchCategoriesText() {
        return searchCategoriesText;
    }

    public void setSearchCategoriesText(String searchCategoriesText) {
        this.searchCategoriesText = searchCategoriesText;
    }

    public Form getSearchFieldForm() {
        return searchFieldForm;
    }

    public void setSearchFieldForm(Form searchFieldForm) {
        this.searchFieldForm = searchFieldForm;
    }

    public SearchCategoryValue getSelectedCategoryValue() {
        return selectedCategoryValue;
    }

    public void setSelectedCategoryValue(SearchCategoryValue selectedValue) {
        this.selectedCategoryValue = selectedValue;
    }

    static public class SearchCategory implements Serializable {
        private static final long serialVersionUID = 1L;
        
        public static final String NAME_ATTR = "name";
        public enum SearchCategoryType {
            ROOT,
            SUBCATEGORY
        };
        
        
        private Map<String, Object> attributes = new HashMap<>();
        private SearchCategoryType searchCategoryType = SearchCategoryType.ROOT;
        private SelectionMode selectionMode = SelectionMode.Single;
        
        public SearchCategory() {
        }
        public SearchCategory(SearchCategoryType searchCategoryType) {
            this.searchCategoryType = searchCategoryType;
        }
        
        public SearchCategory setAttribute(String attributeName, Object value) {
            this.attributes.put(attributeName, value);
            return this;
        }
        
        public Object getAttribute(String attributeName) {
            return this.attributes.get(attributeName);
        }
        public SearchCategoryType getSearchCategoryType() {
            return searchCategoryType;
        }
        public SearchCategory setSearchCategoryType(SearchCategoryType searchCategoryType) {
            this.searchCategoryType = searchCategoryType;
            return this;
        }
        
        public Map<String, Object> getAttributes() {
            return Collections.unmodifiableMap(this.attributes);
        }
        
        public void setAttributes(Map<String, Object> attributes) {
            this.attributes = attributes;
        }
        
        public SelectionMode getSelectionMode() {
            return selectionMode;
        }
        public void setSelectionMode(SelectionMode selectionMode) {
            this.selectionMode = selectionMode;
        }
    }
    
    static public class SearchCategoryValue implements Serializable {
        
        private static final long serialVersionUID = 1L;

        public static final String NAME_ATTR = "name";
        
        private Map<String, Object> attributes = new HashMap<>();
        private List<SearchCategoryValue> values = new ArrayList<>();
        private boolean selected = false;
        private Integer index;
        
        public SearchCategoryValue() {
        }
        
        public SearchCategoryValue(String attributeName, Object attributeValue) {
            this.setAttribute(attributeName, attributeValue);
        }
        
        public SearchCategoryValue(String attributeName, Object attributeValue, boolean selected) {
            this.setAttribute(attributeName, attributeValue);
            this.selected = selected;
        }
        
        public SearchCategoryValue(Integer index, String attributeName, Object attributeValue, boolean selected) {
            this.index = index;
            this.setAttribute(attributeName, attributeValue);
            this.selected = selected;
        }
        
        public List<SearchCategoryValue> getValues() {
            return Collections.unmodifiableList(this.values);
        }
        
        public void addValue(SearchCategoryValue searchCategoryValue) {
            this.values.add(searchCategoryValue);
        }
        
        public void setAttribute(String attributeName, Object value) {
            this.attributes.put(attributeName, value);
        }
        
        public Object getAttribute(String attributeName) {
            return this.attributes.get(attributeName);
        }
       
        public Map<String, Object> getAttributes() {
            return Collections.unmodifiableMap(this.attributes);
        }
        
        public void setAttributes(Map<String, Object> attributes) {
            this.attributes = attributes;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }
        
    }
}
