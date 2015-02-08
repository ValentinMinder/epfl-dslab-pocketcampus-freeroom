// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Linq;
using PocketCampus.Events.Models;
using PocketCampus.Events.ViewModels;
using ThinMvvm;
using Windows.UI.Xaml;

// For some reason I can't get Visibility<-IsEnabled bindings to work on the buttons.
// HACK: Let's do it in code-behind instead.

namespace PocketCampus.Events.Views
{
    public sealed partial class EventPoolView
    {
        public EventPoolView()
        {
            InitializeComponent();

            Loaded += ( _, __ ) =>
            {
                ( (EventPoolViewModel) DataContext ).ListenToProperty( x => x.Pool, UpdateAppBar );
                UpdateAppBar();
            };
        }

        private void UpdateAppBar()
        {
            var pool = ( (EventPoolViewModel) DataContext ).Pool;
            if ( pool == null )
            {
                return;
            }

            var commandButtons = new[] { CategoriesButton, TagsButton, ScanCodeButton, RequestFavoriteEmailButton };

            foreach ( var button in commandButtons )
            {
                button.Visibility = button.Command.CanExecute( null ) ? Visibility.Visible : Visibility.Collapsed;
            }
            SettingsButton.Visibility = pool.Id == EventPool.RootId ? Visibility.Visible : Visibility.Collapsed;

            if ( commandButtons.All( b => b.Visibility == Visibility.Collapsed ) && SettingsButton.Visibility == Visibility.Collapsed )
            {
                BottomAppBar.Visibility = Visibility.Collapsed;
            }
            else
            {
                BottomAppBar.Visibility = Visibility.Visible;
            }
        }
    }
}