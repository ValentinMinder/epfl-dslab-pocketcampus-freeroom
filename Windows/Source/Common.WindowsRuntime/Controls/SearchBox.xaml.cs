// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Windows.Input;
using Windows.UI.Xaml;

namespace PocketCampus.Common.Controls
{
    public sealed partial class SearchBox
    {
        #region Query
        public string Query
        {
            get { return (string) GetValue( QueryProperty ); }
            set { SetValue( QueryProperty, value ); }
        }

        public static readonly DependencyProperty QueryProperty =
            DependencyProperty.Register( "Query", typeof( string ), typeof( SearchBox ), new PropertyMetadata( null ) );
        #endregion

        #region SearchCommand
        public ICommand SearchCommand
        {
            get { return (ICommand) GetValue( SearchCommandProperty ); }
            set { SetValue( SearchCommandProperty, value ); }
        }

        public static readonly DependencyProperty SearchCommandProperty =
            DependencyProperty.Register( "SearchCommand", typeof( ICommand ), typeof( SearchBox ), new PropertyMetadata( null ) );
        #endregion

        #region PlaceholderText
        public string PlaceholderText
        {
            get { return (string) GetValue( PlaceholderTextProperty ); }
            set { SetValue( PlaceholderTextProperty, value ); }
        }

        public static readonly DependencyProperty PlaceholderTextProperty =
            DependencyProperty.Register( "PlaceholderText", typeof( string ), typeof( SearchBox ), new PropertyMetadata( null ) );
        #endregion


        public SearchBox()
        {
            InitializeComponent();
            Root.DataContext = this;
        }
    }
}