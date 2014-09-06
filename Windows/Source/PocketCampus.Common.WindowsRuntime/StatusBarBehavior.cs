using System;
using Microsoft.Xaml.Interactivity;
using Windows.ApplicationModel;
using Windows.UI;
using Windows.UI.ViewManagement;
using Windows.UI.Xaml;

// from http://www.visuallylocated.com/post/2014/04/08/Creating-a-behavior-to-control-the-new-StatusBar-(SystemTray)-in-Windows-Phone-81-XAML-apps.aspx

namespace PocketCampus.Common
{
    public class StatusBarBehavior : DependencyObject, IBehavior
    {
        #region IsVisible
        public bool IsVisible
        {
            get { return (bool) GetValue( IsVisibleProperty ); }
            set { SetValue( IsVisibleProperty, value ); }
        }

        public static readonly DependencyProperty IsVisibleProperty =
            DependencyProperty.Register( "IsVisible", typeof( bool ), typeof( StatusBarBehavior ), new PropertyMetadata( true, OnIsVisibleChanged ) );

        private static async void OnIsVisibleChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            if ( (bool) args.NewValue )
            {
                await StatusBar.GetForCurrentView().ShowAsync();
            }
            else
            {
                await StatusBar.GetForCurrentView().HideAsync();
            }
        }
        #endregion

        #region BackgroundOpacity
        public double BackgroundOpacity
        {
            get { return (double) GetValue( BackgroundOpacityProperty ); }
            set { SetValue( BackgroundOpacityProperty, value ); }
        }

        public static readonly DependencyProperty BackgroundOpacityProperty =
            DependencyProperty.Register( "BackgroundOpacity", typeof( double ), typeof( StatusBarBehavior ), new PropertyMetadata( 0.0, OnBackgroundOpacityChanged ) );

        private static void OnBackgroundOpacityChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            StatusBar.GetForCurrentView().BackgroundOpacity = (double) args.NewValue;
        }
        #endregion

        #region ForegroundColor
        public Color ForegroundColor
        {
            get { return (Color) GetValue( ForegroundColorProperty ); }
            set { SetValue( ForegroundColorProperty, value ); }
        }

        public static readonly DependencyProperty ForegroundColorProperty =
            DependencyProperty.Register( "ForegroundColor", typeof( Color ), typeof( StatusBarBehavior ), new PropertyMetadata( null, OnForegroundColorChanged ) );

        private static void OnForegroundColorChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            StatusBar.GetForCurrentView().ForegroundColor = (Color) args.NewValue;
        }
        #endregion

        #region BackgroundColor
        public Color BackgroundColor
        {
            get { return (Color) GetValue( BackgroundColorProperty ); }
            set { SetValue( BackgroundColorProperty, value ); }
        }

        public static readonly DependencyProperty BackgroundColorProperty =
            DependencyProperty.Register( "BackgroundColor", typeof( Color ), typeof( StatusBarBehavior ), new PropertyMetadata( null, OnBackgroundColorChanged ) );

        private static void OnBackgroundColorChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            StatusBar.GetForCurrentView().BackgroundColor = (Color) args.NewValue;
        }
        #endregion

        #region UseDefaultValues
        public bool UseDefaultValues
        {
            get { return (bool) GetValue( UseDefaultValuesProperty ); }
            set { SetValue( UseDefaultValuesProperty, value ); }
        }

        public static readonly DependencyProperty UseDefaultValuesProperty =
            DependencyProperty.Register( "UseDefaultValues", typeof( bool ), typeof( StatusBarBehavior ), new PropertyMetadata( false, OnUseDefaultValuesChanged ) );

        private static async void OnUseDefaultValuesChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            // N.B.: False is not supported, just don't set it if you don't want the default values.

            // StatusBar.GetForCurrentView throws a REGDB_E_CLASSNOTREG error if ran in the designer...
            if ( DesignMode.DesignModeEnabled )
            {
                return;
            }

            var bar = StatusBar.GetForCurrentView();

            bar.ForegroundColor = (Color) Application.Current.Resources["AppStatusBarForegroundColor"];
            bar.BackgroundColor = (Color) Application.Current.Resources["AppStatusBarBackgroundColor"];
            bar.BackgroundOpacity = 1;
            await bar.ShowAsync();
        }
        #endregion

        public void Attach( DependencyObject associatedObject ) { }
        public void Detach() { }
        public DependencyObject AssociatedObject { get; private set; }
    }

}