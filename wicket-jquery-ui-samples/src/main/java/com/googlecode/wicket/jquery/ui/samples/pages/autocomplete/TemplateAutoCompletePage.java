package com.googlecode.wicket.jquery.ui.samples.pages.autocomplete;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.core.template.IJQueryTemplate;
import com.googlecode.wicket.jquery.ui.form.autocomplete.AutoCompleteTextField;
import com.googlecode.wicket.jquery.ui.form.autocomplete.AutoCompleteUtils;
import com.googlecode.wicket.jquery.ui.panel.JQueryFeedbackPanel;
import com.googlecode.wicket.jquery.ui.samples.data.bean.Genre;

public class TemplateAutoCompletePage extends AbstractAutoCompletePage
{
	private static final long serialVersionUID = 1L;

	public TemplateAutoCompletePage()
	{
		this.init();
	}

	private void init()
	{
		// Model //
		final IModel<Genre> model = new Model<Genre>(Genre.emptyGenre());

		// Form //
		final Form<Void> form = new Form<Void>("form");
		this.add(form);

		// FeedbackPanel //
		final FeedbackPanel feedback = new JQueryFeedbackPanel("feedback");
		form.add(feedback.setOutputMarkupId(true));

		// Auto-complete //
		form.add(new AutoCompleteTextField<Genre>("autocomplete", model) {

			private static final long serialVersionUID = 1L;

			@Override
			protected List<Genre> getChoices(String input)
			{
				return AutoCompleteUtils.contains(input, GENRES);
			}

			@Override
			protected void onSelected(AjaxRequestTarget target)
			{
				info("Your favorite rock genre is: " + this.getModelObject());
				target.add(feedback);
			}

			@Override
			protected IJQueryTemplate newTemplate()
			{
				return new IJQueryTemplate() {

					private static final long serialVersionUID = 1L;

					/**
					 * For an AutoCompleteTextField, the template text should be of the form: <a>...</a>
					 *
					 * The template text will be enclosed in a <script type="text/x-jquery-tmpl" />.
					 * You can use the "\n" character to properly format the template.
					 */
					@Override
					public String getText()
					{
						return  "<a>\n" +
							"<table style='width: 100%' cellspacing='0' cellpadding='0'>\n" +
							" <tr>\n" +
							"  <td>\n" +
							"   <img src='${ coverUrl }' width='50px' />\n" +
							"  </td>\n" +
							"  <td>\n" +
							"   ${ name }\n" +
							"  </td>\n" +
							" </tr>\n" +
							"</table>\n" +
							"</a>";
					}

					@Override
					public List<String> getTextProperties()
					{
						return Arrays.asList("name", "coverUrl");
					}

				};
			}
		});
	}

	// List of Genre(s) //
	static final List<Genre> GENRES = Arrays.asList(
			new Genre("Black Metal", "cover-black-metal.png"),
			new Genre("Death Metal", "cover-death-metal.png"),
			new Genre("Doom Metal", "cover-doom-metal.png"),
			new Genre("Folk Metal", "cover-folk-metal.png"),
			new Genre("Gothic Metal", "cover-gothic-metal.png"),
			new Genre("Heavy Metal", "cover-heavy-metal.png"),
			new Genre("Power Metal", "cover-power-metal.png"),
			new Genre("Symphonic Metal", "cover-symphonic-metal.png"),
			new Genre("Trash Metal", "cover-trash-metal.png"),
			new Genre("Vicking Metal", "cover-vicking-metal.png"));
}
