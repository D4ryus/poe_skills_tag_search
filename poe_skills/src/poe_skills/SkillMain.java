package poe_skills;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * contains some global variables, intialises the CLReader and lets it run
 * @author linc
 *
 */
public class SkillMain extends Application{

	public static final int MAX_NUMBER_OF_SKILLS = 260;
	public static final String POE_WIKI_ADRESS = "http://pathofexile.gamepedia.com/";
	public static final String[] tags = {"Dex", "Str", "Int",
		"Chaos", "Cold", "Fire", "Lightning",
		"Attack", "Cast", "Aura", "Curse", "Spell", "Warcry",
		"Bow", "Movement", "Projectile","Melee", "AoE", 
		"Mine", "Golem", "Minion", "Totem", "Trap",
		"Duration", "Trigger", "Support", "Vaal", "Chaining"};
	private final String[] GROUP_DESCRIPTIONS = {"Attribute", "Element", "Modifier1", "Modifier2", "Minion", "Modifier3"};
	private final int NUMBER_OF_COLUMNS = 9;
	private final int COLUMN_WIDTH = 100;
	private final int COLUMN_SEPARATOR_WIDTH = 20;
	private final int H_GAP = 10;
	private final int V_GAP = 10;
	private final int PADDING_ALL = 25;
	private final int BUTTON_ROW = 7;
	private final int BUTTON_COL = 2;
	private final int ACCORDION_ROW = 9;
	private final int ACCORDION_START_COL = 0;
	private final int ACCORDION_COL_SPAN = 9;
	private final int ACCORDION_ROW_SPAN = 1;
	private final int SEPARATOR_COL_SPAN = 9;
	private final int SEPARATOR_ROW_SPAN = 1;
	private final int SEPARATOR_COL_START = 0;
	private final int SEPARATOR_TAG_BUTTON_ROW = 6;
	private final int SEPARATOR_BUTTON_LIST_ROW = 8;
	private final int SEPARATOR_VERTICAL_COL_SPAN = 1;
	private final int SEPARATOR_VERTICAL_ROW_SPAN = 6;
	private final int SEPARATOR_VERTICAL_COL = 1;
	private final int SEPARATOR_VERTICAL_START_ROW = 0;
	private final int WINDOW_HEIGHT = 600;
	private final int WINDOW_WIDTH = 900;
	private final int[] END_INDEX_OF_CHECKBOX_ROWS = {0, 3, 7, 14, 18, 23, 28};
	private final int NUMBER_OF_CHECKBOX_ROWS = 6;
	private final int CHECKBOXES_START_COL = 2;
	private TitledPane[] skillPanes;
	private ScrollPane skillListScrollPane;
	private Accordion skillAccordion;
	
	public static void main(String[] args) {
		
		launch(args);
		//clReader.run();

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		SkillFilter skillFilter = new SkillFilter();
		
		Scene scene;
		primaryStage.setTitle("PoE Skills Tag Search");
        
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER_LEFT);
		grid.setHgap(H_GAP);
		grid.setVgap(V_GAP);
		grid.setPadding(new Insets(PADDING_ALL));
		//set the width of all columns to COLUMN_WIDTH, except the second which countains only the separator
		for(int counter = 0; counter < NUMBER_OF_COLUMNS; counter++){
			if(counter != 1){
				grid.getColumnConstraints().add(new ColumnConstraints(COLUMN_WIDTH));
			}
			else{
				grid.getColumnConstraints().add(new ColumnConstraints(COLUMN_SEPARATOR_WIDTH));
			}
		}
		
		//the descriptions at the side
		for(int counter = 0; counter < GROUP_DESCRIPTIONS.length; counter++){
			grid.add(new Label(GROUP_DESCRIPTIONS[counter]), 0, counter);
		}
		
		final CheckBox[] tagCheckBoxes = new CheckBox[tags.length];
		
		//create and initialiase the Checkboxes
		for(int counter = 0; counter < tags.length; counter++){
			tagCheckBoxes[counter] = new CheckBox(tags[counter]);
		}
		
		int colCounter = CHECKBOXES_START_COL;
		for(int rowCounter = 0; rowCounter < NUMBER_OF_CHECKBOX_ROWS; rowCounter ++){
			for(int counter = END_INDEX_OF_CHECKBOX_ROWS[rowCounter]; counter < END_INDEX_OF_CHECKBOX_ROWS[rowCounter + 1]; counter++){
				grid.add(tagCheckBoxes[counter], colCounter, rowCounter);
				addCheckBoxHandler(tags[counter], tagCheckBoxes[counter], skillFilter);
				colCounter++;
			}
			colCounter = CHECKBOXES_START_COL;
		}
		
		Separator separatorTagsButton = new Separator(Orientation.HORIZONTAL);
		GridPane.setConstraints(separatorTagsButton, SEPARATOR_COL_START, SEPARATOR_TAG_BUTTON_ROW, SEPARATOR_COL_SPAN, SEPARATOR_ROW_SPAN);
		grid.add(separatorTagsButton, SEPARATOR_COL_START, SEPARATOR_TAG_BUTTON_ROW);
		Separator separatorButtonList = new Separator(Orientation.HORIZONTAL);
		GridPane.setConstraints(separatorButtonList, SEPARATOR_COL_START, SEPARATOR_BUTTON_LIST_ROW, SEPARATOR_COL_SPAN, SEPARATOR_ROW_SPAN);
		grid.add(separatorButtonList, SEPARATOR_COL_START, SEPARATOR_BUTTON_LIST_ROW);
		Separator separatorVertical = new Separator(Orientation.VERTICAL);
		GridPane.setConstraints(separatorVertical, SEPARATOR_VERTICAL_COL, SEPARATOR_VERTICAL_START_ROW, SEPARATOR_VERTICAL_COL_SPAN, SEPARATOR_VERTICAL_ROW_SPAN);
		grid.add(separatorVertical, SEPARATOR_VERTICAL_COL, SEPARATOR_VERTICAL_START_ROW);

		//the button that triggers the display of the skills matching the criteria
		Button submitButton = new Button("show Skills");
		submitButton.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		        //skillFilter.showSkills();
		    	searchSkills(grid, skillFilter);
		    }
		});
		grid.add(submitButton, BUTTON_COL, BUTTON_ROW);
		
		//set scene and show the stage
		scene = new Scene(grid, WINDOW_WIDTH, WINDOW_HEIGHT);
		primaryStage.setScene(scene);
		
        primaryStage.show();
		
	}
	
	
	private void addCheckBoxHandler(String tagName, CheckBox checkBox, SkillFilter skillFilter){
		final String tagNameFinal = tagName;
		final CheckBox checkBoxFinal = checkBox;
		checkBoxFinal.selectedProperty().addListener(new ChangeListener<Boolean>() {
	        public void changed(ObservableValue<? extends Boolean> ov,
	            Boolean old_val, Boolean new_val) {
	        		if(new_val){
	        			skillFilter.addCriterium(tagNameFinal);
	        		}
	        		else{
	        			skillFilter.removeCriterium(tagNameFinal);
	        		}
	        }
	    });
	}
	
	private void searchSkills(GridPane grid, SkillFilter skillFilter){
		if(skillListScrollPane != null){
			grid.getChildren().remove(skillListScrollPane);
		}
		skillAccordion = new Accordion();
		ArrayList<Skill> matchedSkills = new ArrayList<Skill>();
		try {
			matchedSkills = skillFilter.filter();
		} catch (NoSuchFilterCriteriumException e) {
			System.err.println("Could not find the prime belonging to criterium " + e.getMissingCriterium());
			System.exit(0);
		}
		skillPanes = new TitledPane[matchedSkills.size()];
		TitledPane skillPane;
		Skill currentSkill;
		for(int counter = 0; counter < matchedSkills.size(); counter++){
			skillPane = new TitledPane();
			currentSkill = matchedSkills.get(counter);
			skillPane.setText(currentSkill.getSkillName());
			GridPane paneGrid = new GridPane();
			String skillTags = "Tags: ";
			for(String tag: currentSkill.getAttributes()){
				skillTags = skillTags + tag + "  ";
			}
			skillTags += "\n";
			paneGrid.add(new Text(skillTags + currentSkill.getSkillDescription()), 0, 0);
			paneGrid.add(new Hyperlink(POE_WIKI_ADRESS + currentSkill.getSkillName().replace(" ", "_")), 0, 1);
			skillPane.setContent(paneGrid);
			skillPane.setExpanded(false);
			skillPanes[counter] = skillPane;
		}
		skillAccordion.getPanes().addAll(skillPanes);
		skillListScrollPane = new ScrollPane(skillAccordion);
		grid.add(skillListScrollPane, ACCORDION_START_COL, ACCORDION_ROW, ACCORDION_COL_SPAN, ACCORDION_ROW_SPAN);
	}

}
