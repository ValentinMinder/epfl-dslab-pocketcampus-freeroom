package org.pocketcampus.plugin.positioning;

import org.pocketcampus.shared.plugin.map.Position;

import android.content.Context;

public class HandOver {
	
	private Grid grid_;
	private Position HORposition_;
	private Context ctx_;
	
	public HandOver(Context ctx){
		
		this.ctx_ = ctx;
		this.grid_ = new Grid(ctx_);
		this.HORposition_ = getHORPosition();
	}

	private Position getHORPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
