// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Linq;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Markup;

namespace PocketCampus.Common.Controls
{
    [ContentProperty( Name = "Cases" )]
    public sealed class Switch : ContentControl
    {
        #region Value
        public object Value
        {
            get { return GetValue( ValueProperty ); }
            set { SetValue( ValueProperty, value ); }
        }

        public static readonly DependencyProperty ValueProperty =
            DependencyProperty.Register( "Value", typeof( object ), typeof( Switch ), new PropertyMetadata( null, ( o, _ ) => ( (Switch) o ).Update() ) );
        #endregion

        #region Cases
        public IList<SwitchCase> Cases
        {
            get { return (IList<SwitchCase>) GetValue( CasesProperty ); }
            set { SetValue( CasesProperty, value ); }
        }

        public static readonly DependencyProperty CasesProperty =
            DependencyProperty.Register( "Cases", typeof( IList<SwitchCase> ), typeof( Switch ), new PropertyMetadata( null ) );
        #endregion


        public Switch()
        {
            // If Cases is set in PropertyMetadata, it'll be the same list for all instances
            Cases = new List<SwitchCase>();
            DefaultStyleKey = typeof( Switch );

            Loaded += ( _, __ ) => Update();
        }


        private void Update()
        {
            if ( Value == null || Cases.Count == 0 )
            {
                return;
            }

            string value = Value.ToString();
            var matchingCase = Cases.FirstOrDefault( c => c.ValuesSet.Contains( value ) );
            Content = matchingCase == null ? null : matchingCase.Content;
        }
    }

    [ContentProperty( Name = "Content" )]
    public sealed class SwitchCase : DependencyObject
    {
        private static readonly char[] Separators = { ' ', ',' };


        #region Values
        public string Values
        {
            get { return (string) GetValue( ValuesProperty ); }
            set { SetValue( ValuesProperty, value ); }
        }

        public static readonly DependencyProperty ValuesProperty =
            DependencyProperty.Register( "Values", typeof( string ), typeof( SwitchCase ), new PropertyMetadata( null, OnValuesChanged ) );

        private static void OnValuesChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            ( (SwitchCase) obj ).ValuesSet = new HashSet<string>( ( (string) args.NewValue ).Split( Separators, StringSplitOptions.RemoveEmptyEntries ) );
        }
        #endregion

        #region Content
        public object Content
        {
            get { return GetValue( ContentProperty ); }
            set { SetValue( ContentProperty, value ); }
        }

        public static readonly DependencyProperty ContentProperty =
            DependencyProperty.Register( "Content", typeof( object ), typeof( SwitchCase ), new PropertyMetadata( null ) );
        #endregion


        internal ISet<string> ValuesSet { get; private set; }
    }
}