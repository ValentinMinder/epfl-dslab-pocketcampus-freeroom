// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using Microsoft.Xaml.Interactivity;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace PocketCampus.Common
{
    public sealed class FocusBehavior : DependencyObject, IBehavior
    {
        public DependencyObject AssociatedObject { get; private set; }

        public void Attach( DependencyObject associatedObject )
        {
            AssociatedObject = associatedObject;

            var associatedControl = (Control) associatedObject;
            associatedControl.Loaded += ( _, __ ) => associatedControl.Focus( FocusState.Pointer );
        }

        public void Detach()
        {
            AssociatedObject = null;
        }
    }
}