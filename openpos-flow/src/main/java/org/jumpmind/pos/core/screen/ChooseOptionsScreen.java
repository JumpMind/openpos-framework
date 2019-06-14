package org.jumpmind.pos.core.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChooseOptionsScreen extends DynamicFormScreen {
    private static final long serialVersionUID = 1L;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public enum DisplayStyle {
        ButtonList,
        ButtonGrid
    }
    
    private List<OptionItem> options;
    private SelectionMode selectionMode = SelectionMode.Single;
    private DisplayStyle displayStyle = DisplayStyle.ButtonList;
    private String promptText;
    
    private List<OptionItem> additionalButtons;
    
    public ChooseOptionsScreen( ) {
        this( new ArrayList<OptionItem>() );
    }
   
    public ChooseOptionsScreen( List<OptionItem> options ) {
        this( options, SelectionMode.Single );
    }

    public ChooseOptionsScreen( List<OptionItem> options, SelectionMode selectionMode ) {
        setScreenType(ScreenType.ChooseOptions);
        this.options = options;
        this.selectionMode = selectionMode;
    }
    
    public void addOption( OptionItem option ) {
        this.getOptions().add( option );
    }
    
    public List<OptionItem> getOptions() {
        return options;
    }
    public void setOptions(List<OptionItem> options) {
        this.options = options;
    }
    
    public SelectionMode getSelectionMode() {
        return selectionMode;
    }
    
    public void setSelectionMode(SelectionMode selectionMode) {
        this.selectionMode = selectionMode;
    }

    public Optional<OptionItem> getSelectedOption() {
        List<OptionItem> selectedOptions = this.getSelectedOptions();
        Optional<OptionItem> selectedItem = Optional.empty();
        if ( selectedOptions.size() > 0 ) {
            if ( SelectionMode.Multiple == this.getSelectionMode() ) {
                if ( selectedOptions.size() > 1 ) {
                    logger.warn( "Selection mode is {}, but there are multiple items selected.  Returning only first OptionItem.", this.selectionMode );
                }
            }   
            selectedItem = Optional.of( selectedOptions.get(0) );
        }
        
        return selectedItem;
    }

    public List<OptionItem> getSelectedOptions() {
        List<OptionItem> selectedOptions = new ArrayList<>();
        Optional<List<OptionItem>> allOptions = Optional.ofNullable( this.options );
        allOptions.ifPresent(  optList -> {
            optList.stream().filter( o -> o.isSelected() )
            .forEachOrdered( o -> selectedOptions.add( o ) );
        } );

        if ( SelectionMode.Single == this.getSelectionMode() && selectedOptions.size() > 1 ) {
            logger.warn( "Selection mode is {}, but there are multiple items selected.  Returning only first OptionItem.", this.selectionMode );
            selectedOptions.removeIf( o -> o != selectedOptions.get( 0 ) ); // remove all but first item
        }
        
        return selectedOptions;
    }

    public String getPromptText() {
        return promptText;
    }

    public void setPromptText(String text) {
        this.promptText = text;
    }

    public DisplayStyle getDisplayStyle() {
        return displayStyle;
    }

    public void setDisplayStyle(DisplayStyle displayStyle) {
        this.displayStyle = displayStyle;
    }

    public List<OptionItem> getAdditionalButtons() {
        return additionalButtons;
    }

    public void setAdditionalButtons(List<OptionItem> additionalButtons) {
        this.additionalButtons = additionalButtons;
    }

}
