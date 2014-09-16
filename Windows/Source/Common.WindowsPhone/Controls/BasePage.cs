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

            Loaded += ( s, e ) =>
            {
                if ( ApplicationBar != null )
                {
                    // HACK to avoid strange problems with 1080p WP 8.1 devices
                    // see http://social.msdn.microsoft.com/Forums/wpapps/en-US/4a82edc5-273b-4655-95d6-aee6e953aaaa/1920x1080-black-border-at-the-bottom?forum=wpdevelop
                    ApplicationBar.Opacity = 0.99;
                }
            };
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