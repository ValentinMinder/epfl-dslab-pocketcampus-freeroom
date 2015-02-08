// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Windows.Input;
using ThinMvvm;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace PocketCampus.Common.Controls
{
    public sealed class CacheStatusDisplay : Control
    {
        #region Status
        public CacheStatus Status
        {
            get { return (CacheStatus) GetValue( StatusProperty ); }
            set { SetValue( StatusProperty, value ); }
        }

        public static readonly DependencyProperty StatusProperty =
            DependencyProperty.Register( "Status", typeof( CacheStatus ), typeof( CacheStatusDisplay ), new PropertyMetadata( CacheStatus.NoData, OnStatusChanged ) );

        private static void OnStatusChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            ( (CacheStatusDisplay) obj ).Visibility = (CacheStatus) args.NewValue == CacheStatus.Used ? Visibility.Visible : Visibility.Collapsed;
        }
        #endregion

        #region RetryCommand
        public ICommand RetryCommand
        {
            get { return (ICommand) GetValue( RetryCommandProperty ); }
            set { SetValue( RetryCommandProperty, value ); }
        }

        public static readonly DependencyProperty RetryCommandProperty =
            DependencyProperty.Register( "RetryCommand", typeof( ICommand ), typeof( CacheStatusDisplay ), new PropertyMetadata( null ) );
        #endregion


        public CacheStatusDisplay()
        {
            DefaultStyleKey = typeof( CacheStatusDisplay );
        }
    }
}