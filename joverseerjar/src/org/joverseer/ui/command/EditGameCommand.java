package org.joverseer.ui.command;

import org.joverseer.joApplication;
import org.joverseer.game.Game;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.NewGame;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.views.NewGameForm;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.FormModelHelper;

public class EditGameCommand extends ActionCommand {
    public EditGameCommand() {
        super("editGameCommand");
    }

    @Override
	protected void doExecuteCommand() {
    	if (!ActiveGameChecker.checkActiveGameExists()) return;
    	final Game g = GameHolder.instance().getGame();
        final NewGame ng = new NewGame();
        ng.setGameType(g.getMetadata().getGameType());
        ng.setNationNo(g.getMetadata().getNationNo());
        ng.setNumber(g.getMetadata().getGameNo());
        ng.setNewXmlFormat(g.getMetadata().getNewXmlFormat());
        FormModel formModel = FormModelHelper.createFormModel(ng);
        final NewGameForm form = new NewGameForm(formModel, true);
        final FormBackedDialogPage page = new FormBackedDialogPage(form);

        final TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
            @Override
			protected void onAboutToShow() {
                setDescription(Messages.getString(form.getId() + ".description"));
            }

            @Override
			protected boolean onFinish() {
                form.commit();
                
                GameMetadata gm = g.getMetadata();
                gm.setGameNo(ng.getNumber());
                gm.setNationNo(ng.getNationNo());
                gm.setGameType(ng.getGameType());
                gm.setAdditionalNations(ng.getAdditionalNations());
                gm.setNewXmlFormat(ng.getNewXmlFormat());
                
                GameHolder gh = GameHolder.instance();
                gh.setGame(g);
                
                joApplication.publishEvent(LifecycleEventsEnum.GameChangedEvent, g, this);

                return true;
            }
        };
        dialog.setTitle(Messages.getString("editGameDialog.title"));
        dialog.showDialog();

    }
}
