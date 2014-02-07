// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Windows;
using Microsoft.Phone.Controls;

namespace PocketCampus.Common.Controls
{
    /// <summary>
    /// Base class for all pages.
    /// Contains animations and orientation visual states.
    /// </summary>
    [TemplateVisualState( GroupName = "OrientationStates", Name = "Portrait" )]
    [TemplateVisualState( GroupName = "OrientationStates", Name = "Landscape" )]
    public class BasePage : PhoneApplicationPage
    {
        /// <summary>
        /// Creates a new BasePage.
        /// </summary>
        public BasePage()
        {
            TransitionService.SetNavigationInTransition( this, new NavigationInTransition
            {
                Backward = new TurnstileTransition { Mode = TurnstileTransitionMode.BackwardIn },
                Forward = new TurnstileTransition { Mode = TurnstileTransitionMode.ForwardIn }
            } );
            TransitionService.SetNavigationOutTransition( this, new NavigationOutTransition
            {
                Backward = new TurnstileTransition { Mode = TurnstileTransitionMode.BackwardOut },
                Forward = new TurnstileTransition { Mode = TurnstileTransitionMode.ForwardOut }
            } );

            Style = (Style) Application.Current.Resources["AppPageStyle"];

            // TODO: If (when?) the PhoneThemeManager is updated to include an option to override the app bar only, remove this
            Loaded += ( s, e ) => ThemeManager.MatchOverriddenTheme( this.ApplicationBar );
        }


        protected override void OnOrientationChanged( OrientationChangedEventArgs e )
        {
            base.OnOrientationChanged( e );
            if ( e.Orientation.HasFlag( PageOrientation.Portrait ) )
            {
                VisualStateManager.GoToState( this, "Portrait", true );
            }
            else if ( e.Orientation.HasFlag( PageOrientation.Landscape ) )
            {
                VisualStateManager.GoToState( this, "Landscape", true );
            }
        }
    }
}