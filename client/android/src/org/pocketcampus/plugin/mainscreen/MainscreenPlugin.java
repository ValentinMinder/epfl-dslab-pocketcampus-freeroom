package org.pocketcampus.plugin.mainscreen;

import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.Core;
import org.pocketcampus.core.plugin.Icon;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainscreenPlugin extends PluginBase {
	private Context ctx_;
	private Core core_;
	private Vector<PluginBase> plugins_;

	public static Intent createIntent(Context context) {
		Intent i = new Intent(context, MainscreenPlugin.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return i;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mainscreen_main);

		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle(getResources().getString(R.string.app_name));

		ctx_ = this.getApplicationContext();
		core_ = Core.getInstance();
		plugins_ = core_.getAvailablePlugins();

		LinearLayout layout = (LinearLayout) findViewById(R.id.MenuLayout);
		LinearLayout infoLayout = (LinearLayout) findViewById(R.id.InfoLayout);

		for (final PluginBase plugin : plugins_) {
			PluginInfo pluginInfo = plugin.getPluginInfo();
			
			if(pluginInfo.hasMenuIcon() == true) {
				// LAYOUT //
				RelativeLayout relLayout = new RelativeLayout(ctx_);
				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				relLayout.setLayoutParams(layoutParams);
				
				
				// ICON //
				ImageButton button = new ImageButton(ctx_);

				button.setOnClickListener(new View.OnClickListener() {
		             public void onClick(View v) {
		            	 core_.displayPlugin(ctx_, plugin);
		             }
		         });

				// icon
				if(pluginInfo.getIcon() != null) {
					button.setImageDrawable(pluginInfo.getIcon().getDrawable(ctx_));
				} else {
					button.setImageDrawable(Icon.getDefaultDrawable(ctx_));
				}

				RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				buttonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
				button.setLayoutParams(buttonParams);
				button.setBackgroundColor(0x00000000);
				button.setId(1);
				relLayout.addView(button);
				
				// LABEL //
				TextView text = new TextView(ctx_);
				text.setText(pluginInfo.getName());
				
				RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				
				textParams.addRule(RelativeLayout.BELOW, 1);
				textParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
				text.setLayoutParams(textParams);
				text.setTextColor(0xff444444);
				text.setGravity(Gravity.BOTTOM);
				relLayout.addView(text);
				
				
				// DONE //
				relLayout.setPadding(1, 15, 1, 15);
				layout.addView(relLayout);
			}

			// Configure button
//			Button configureButton = new Button(ctx_);
//			configureButton.setText(getString(R.string.mainscreen_configure)+ " " + plugin.getPluginInfo().getId());
//
//			if(plugin.getPluginPreference() != null) {
//				configureButton.setOnClickListener(new View.OnClickListener() {
//		             public void onClick(View v) {
//		            	 core_.configurePlugin(ctx_, plugin);
//		             }
//		         });
//			} else {
//				configureButton.setEnabled(false);
//			}
//			
//			configureButton.setBackgroundColor(0x00000000);
//			infoLayout.addView(configureButton);
			
			if(plugin.getPluginPreference() != null) {
				RelativeLayout infoItemLayout = new RelativeLayout(ctx_);
				
				RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				iconParams.addRule(RelativeLayout.CENTER_VERTICAL);
				
				ImageView icon = new ImageView(ctx_);
				icon.setLayoutParams(iconParams);
				icon.setImageDrawable(pluginInfo.getMiniIcon().getDrawable(ctx_));
				icon.setId(2);
				icon.setPadding(6, 0, 12, 0);
				infoItemLayout.addView(icon);
				
				RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				titleParams.addRule(RelativeLayout.RIGHT_OF, 2);
				titleParams.addRule(RelativeLayout.ALIGN_BOTTOM);
				
				TextView title = new TextView(ctx_);
				title.setText("Configure " + pluginInfo.getName());
				title.setLayoutParams(titleParams);
				title.setTextColor(0xff444444);
				title.setTypeface(Typeface.DEFAULT_BOLD);
				infoItemLayout.addView(title);
				
				RelativeLayout.LayoutParams detailParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				detailParams.addRule(RelativeLayout.BELOW, 2);
				detailParams.addRule(RelativeLayout.RIGHT_OF, 2);
				
				TextView detail = new TextView(ctx_);
				detail.setText("Id: " +pluginInfo.getId()+ ", hasMenuIcon: " +pluginInfo.hasMenuIcon());
				detail.setTextColor(0xff696969);
				detail.setLayoutParams(detailParams);
				infoItemLayout.addView(detail);
				
				infoItemLayout.setPadding(6, 12, 5, 5);
				infoLayout.addView(infoItemLayout);
				
				infoItemLayout.setOnClickListener(new View.OnClickListener() {
		             public void onClick(View v) {
		            	 core_.configurePlugin(ctx_, plugin);
		             }
		         });
			}
			
		}
	}

	@Override
	public PluginPreference getPluginPreference() {
		return null;
	}

	@Override
	public PluginInfo getPluginInfo() {
		return new MainscreenInfo();
	}
}