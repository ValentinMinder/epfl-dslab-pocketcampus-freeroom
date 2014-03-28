// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Windows;
using System.Windows.Controls;
using PocketCampus.Common;

namespace PocketCampus.Main
{
    /// <summary>
    /// Converts WP plugins to ControlTemplates for their icons.
    /// </summary>
    // HACK: Using reflection because design data can't reference WP plugins...
    public sealed class PluginToIconTemplateConverter : ValueConverter<object, ControlTemplate>
    {
        public override ControlTemplate Convert( object value )
        {
            var prop = value.GetType().GetProperty( "IconKey" );
            if ( prop == null )
            {
                return null;
            }
            string key = (string) prop.GetValue( value, null );
            return (ControlTemplate) Application.Current.Resources[key];
        }
    }
}