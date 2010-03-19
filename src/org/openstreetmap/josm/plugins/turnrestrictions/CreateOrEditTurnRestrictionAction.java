package org.openstreetmap.josm.plugins.turnrestrictions;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.logging.Logger;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.turnrestrictions.editor.TurnRestrictionEditor;
import org.openstreetmap.josm.plugins.turnrestrictions.editor.TurnRestrictionEditorManager;
import org.openstreetmap.josm.plugins.turnrestrictions.editor.TurnRestrictionSelectionPopupPanel;
import org.openstreetmap.josm.tools.Shortcut;

/**
 * This action is triggered by a global shortcut (default is Shift-Ctrl-T on windows). 
 * Depending on the current selection it either launches an editor for a new turn
 * restriction or a popup component from which one can choose a turn restriction to
 * edit. 
 *
 */
public class CreateOrEditTurnRestrictionAction extends JosmAction {
	static private final Logger logger = Logger.getLogger(CreateOrEditTurnRestrictionAction.class.getName());
	public CreateOrEditTurnRestrictionAction() {
		super(
		    tr("Create/Edit turn restriction..."),
		    null,
		    tr("Create or edit a turn restriction."),
		    Shortcut.registerShortcut(
					"turnrestrictions:create-or-edit", 
					tr("Turnrestrictions: Create or Edit"),
					// results in Shift-Ctrl-T on windows
					KeyEvent.VK_T,
					Shortcut.GROUPS_ALT1+Shortcut.GROUP_HOTKEY, 
					Shortcut.SHIFT_DEFAULT
			),
			true /* register action */
	    );
	}	
	
	public void actionPerformed(ActionEvent e) {
		OsmDataLayer layer = Main.main.getEditLayer();
		if (layer == null) return;
		Collection<Relation> trs = TurnRestrictionSelectionPopupPanel.getTurnRestrictionsParticipatingIn(layer.data.getSelected());
		if (layer == null) return;
		if (trs.isEmpty()){
			// current selection isn't participating in turn restrictions. Launch
			// an editor for a new turn restriction 
			//
			Relation tr = new TurnRestrictionBuilder().buildFromSelection(layer);
			TurnRestrictionEditor editor = new TurnRestrictionEditor(Main.map.mapView,layer,tr);
			TurnRestrictionEditorManager.getInstance().positionOnScreen(editor);
			TurnRestrictionEditorManager.getInstance().register(layer, tr, editor);
			editor.setVisible(true);
		} else {
			// let the user choose whether he wants to create a new turn restriction or
			// edit one of the turn restrictions participating in the current selection 
			TurnRestrictionSelectionPopupPanel pnl = new TurnRestrictionSelectionPopupPanel(
					layer
			);
			pnl.launch();
		}
	}
}
