// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Linq;
using Microsoft.Phone.Shell;
using PocketCampus.Common.Controls;
using PocketCampus.Main.Resources;
using ThinMvvm;

namespace PocketCampus.Main.Views
{
    public partial class SettingsView : BasePage
    {
        private const string ColoredSmallBackgroundImage = "Assets/Tiles/ColoredSmall.png";
        private const string ColoredBackgroundImage = "Assets/Tiles/ColoredMedium.png";
        private const string WhiteSmallBackgroundImage = "Assets/Tiles/WhiteSmall.png";
        private const string WhiteBackgroundImage = "Assets/Tiles/WhiteMedium.png";

        private const string PluginKey = "Main";
        private const string UseColoredTileSetting = "UseColoredTile";

        public SettingsView()
        {
            InitializeComponent();

            // HACK: Not very clean, but it works...
            var settings = (ApplicationSettings) Container.Get( typeof( ApplicationSettings ) );
            bool currentValue = true;
            if ( settings.IsDefined( PluginKey, UseColoredTileSetting ) )
            {
                currentValue = settings.Get<bool>( PluginKey, UseColoredTileSetting );
            }

            UseColoredTileBox.IsChecked = currentValue;

            UseColoredTileBox.Checked += ( _, __ ) => ChangeApplicationTile( true );
            UseColoredTileBox.Unchecked += ( _, __ ) => ChangeApplicationTile( false );
        }

        private void ChangeApplicationTile( bool useColor )
        {
            var settings = (ApplicationSettings) Container.Get( typeof( ApplicationSettings ) );
            settings.Set( PluginKey, UseColoredTileSetting, useColor );

            ShellTile.ActiveTiles.First().Update( new FlipTileData
            {
                BackgroundImage = new Uri( useColor ? ColoredBackgroundImage : WhiteBackgroundImage, UriKind.Relative ),
                SmallBackgroundImage = new Uri( useColor ? ColoredSmallBackgroundImage : WhiteSmallBackgroundImage, UriKind.Relative ),
                Title = AppResources.ApplicationTitle
            } );
        }
    }
}