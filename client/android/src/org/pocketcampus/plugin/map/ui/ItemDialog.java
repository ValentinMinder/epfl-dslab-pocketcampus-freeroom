package org.pocketcampus.plugin.map.ui;

import org.osmdroid.views.overlay.OverlayItem;
import org.pocketcampus.R;
import org.pocketcampus.plugin.map.MapPlugin;
import org.pocketcampus.plugin.map.utils.GeoPointConverter;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Show a dialog box when the user clicks on an item on the map
 * 
 * @status WIP
 * 
 * @author Jonas
 *
 */
public class ItemDialog extends Dialog {
	
	private MapPlugin mp_;
	
	public ItemDialog(final MapPlugin mp, final OverlayItem item) {
		super(mp);
		
		this.mp_ = mp;
		
		setContentView(R.layout.map_item_dialog);
		setCancelable(true);
		setCanceledOnTouchOutside(true);
		
		setTitle(item.getTitle());

		String description = item.getSnippet();
		
		TextView text = (TextView) findViewById(R.id.map_item_description);
		
		if(description == null || "".equals(description)) {
			text.setVisibility(View.GONE);
		} else {
			text.setText(description);
		}

//		ImageView img = (ImageView) dialog.findViewById(R.id.map_dialog_image);
//		img.setImageDrawable(item.getDrawable());
		
		Button take = (Button) findViewById(R.id.map_take_me_there_button);
		take.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mp_.showDirectionsFromHereToPosition(GeoPointConverter.toPosition(item.getPoint()));
				dismiss();
			}
		});
		
		Button zoom = (Button) findViewById(R.id.map_zoom_button);
		zoom.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mp_.centerOnPoint(item.getPoint());
				dismiss();
			}
		});
	}
	
}
