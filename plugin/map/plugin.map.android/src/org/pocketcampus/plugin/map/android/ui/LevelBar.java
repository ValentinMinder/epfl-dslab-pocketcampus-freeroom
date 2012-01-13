package org.pocketcampus.plugin.map.android.ui;

import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * Level bar (represented by a seek bar)
 * It allows to change the level of the map
 * When a change of level happens, it calls the method of the listener
 * 
 * @author Florian <florian.laurent@epfl.ch>
 * @author Johan <johan.leuenberger@epfl.ch>
 *
 */
public class LevelBar implements OnSeekBarChangeListener{

	private SeekBar seekBar_;
	private OnLevelBarChangeListener listener_;
	private int currentLevel_;
	private int maxLevel_;
	private int minLevel_;
	
	/**
	 * Creates a new LevelBar
	 * @param seekLevelBar the corresponding SeekBar (they are linked)
	 * @param listener the listener
	 * @param max the maximum level (for example 3)
	 * @param min the minimum level (for example -2)
	 * @param default_level the default level
	 */
	public LevelBar(SeekBar seekLevelBar, OnLevelBarChangeListener listener, int max, int min, int default_level) {
		this.seekBar_ = seekLevelBar;
		this.listener_ = listener;
		
		this.maxLevel_ = max;
		this.minLevel_ = min;
		this.currentLevel_ = default_level;
		
		seekBar_.setVisibility(View.VISIBLE);
		seekBar_.setMax(maxLevel_ - minLevel_);
		seekBar_.setProgress(default_level - minLevel_);
		seekBar_.setOnSeekBarChangeListener(this);
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		currentLevel_ = progress + minLevel_;
		listener_.onLevelChanging(currentLevel_);
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		//NOTHING TO DO
	}
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		//Callback!
		listener_.onLevelChanged(currentLevel_);
	}
	
}
