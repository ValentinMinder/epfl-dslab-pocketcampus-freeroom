package org.pocketcampus.plugin.mainscreen;

import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.Core;
import org.pocketcampus.core.plugin.DisplayBase;
import org.pocketcampus.core.plugin.Icon;
import org.pocketcampus.core.plugin.PluginDescriptor;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class MainscreenDisplay extends DisplayBase {
	private Context ctx_;
	private Core core_;
	private Vector<PluginDescriptor> plugins_;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainscreen_main);
		
		ctx_ = this.getApplicationContext();
		core_ = Core.getInstance();
		plugins_ = core_.getAvailablePlugins();
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.MenuLayout);
		LinearLayout infoLayout = (LinearLayout) findViewById(R.id.InfoLayout);
		
		for (final PluginDescriptor plugin : plugins_) {
			// ICONS //
			if(plugin.getDisplayClass() != null) {
				ImageButton button = new ImageButton(ctx_);
				
				// display class
				button.setOnClickListener(new View.OnClickListener() {
		             public void onClick(View v) {
		            	 core_.displayPlugin(ctx_, plugin);
		             }
		         });
				
				// icon
				if(plugin.getIcon() != null) {
					button.setImageDrawable(plugin.getIcon().getDrawable(ctx_));
				} else {
					button.setImageDrawable(Icon.getDefaultDrawable(ctx_));
				}
				
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				button.setLayoutParams(params);
				layout.addView(button);
			}

			// Configure button
			Button configureButton = new Button(ctx_);
			configureButton.setText(getString(R.string.mainscreen_configure)+ " " + plugin.getId());
			
			if(plugin.getConfigurationClass()!=null) {
				configureButton.setOnClickListener(new View.OnClickListener() {
		             public void onClick(View v) {
		            	 core_.configurePlugin(ctx_, plugin);
		             }
		         });
			} else {
				configureButton.setEnabled(false);
			}
			
			infoLayout.addView(configureButton);
		}
	}
}