package org.pocketcampus.plugin.mainscreen;

import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.core.Core;
import org.pocketcampus.core.DisplayBase;
import org.pocketcampus.core.IInfoProviderService;
import org.pocketcampus.core.PluginDescriptor;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainscreenDisplay extends DisplayBase {
	private Context ctx_;
	private Core core_;
	private Vector<PluginDescriptor> plugins_;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainscreen);
		
		ctx_ = this.getApplicationContext();
		core_ = Core.getInstance();
		plugins_ = core_.getAvailablePlugins();
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.Layout);
		
		for (final PluginDescriptor plugin : plugins_) {
			// Title
			String informationProvider = "";
			if(plugin.getDisplayClass()!=null && IInfoProviderService.class.isAssignableFrom(plugin.getDisplayClass())) {
				informationProvider = ", information provider";
			}
			
			TextView pluginName = new TextView(ctx_);
			pluginName.setText(plugin.getName() + " (id:" + plugin.getId() + informationProvider + ")");
			layout.addView(pluginName);
			
			// Launch button
			Button launchButton = new Button(ctx_);
			launchButton.setText("launch");
			
			if(plugin.getDisplayClass()!=null) {
				launchButton.setOnClickListener(new View.OnClickListener() {
		             public void onClick(View v) {
		            	 core_.displayPlugin(ctx_, plugin);
		             }
		         });
			} else {
				launchButton.setEnabled(false);
			}
			
			layout.addView(launchButton);
			
			// Configure button
			Button configureButton = new Button(ctx_);
			configureButton.setText("configure");
			
			if(plugin.getConfigurationClass()!=null) {
				configureButton.setOnClickListener(new View.OnClickListener() {
		             public void onClick(View v) {
		            	 core_.configurePlugin(ctx_, plugin);
		             }
		         });
			} else {
				configureButton.setEnabled(false);
			}
			
			layout.addView(configureButton);
		}
		
		
		
	}
}










