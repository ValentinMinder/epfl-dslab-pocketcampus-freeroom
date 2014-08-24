// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using Microsoft.Phone.Controls;
using ThinMvvm;

namespace PocketCampus.Common.Controls
{
    /// <summary>
    /// TextBox with search and asynchronous auto-complete functionality.
    /// </summary>
    public partial class SearchBox : ObservableControl
    {
        #region AutoCompleteProvider DependencyProperty
        /// <summary>
        /// Asynchronously provides auto-complete suggestions from a query.
        /// </summary>
        public Func<string, Task<IEnumerable<string>>> AutoCompleteProvider
        {
            get { return (Func<string, Task<IEnumerable<string>>>) GetValue( AutoCompleteProviderProperty ); }
            set { SetValue( AutoCompleteProviderProperty, value ); }
        }

        public static readonly DependencyProperty AutoCompleteProviderProperty =
            DependencyProperty.Register( "AutoCompleteProvider", typeof( Func<string, Task<IEnumerable<string>>> ), typeof( SearchBox ), new PropertyMetadata( null ) );
        #endregion

        #region SearchCommand DependencyProperty
        /// <summary>
        /// The command executed when the user taps the search button.
        /// </summary>
        public ICommand SearchCommand
        {
            get { return (ICommand) GetValue( SearchCommandProperty ); }
            set { SetValue( SearchCommandProperty, value ); }
        }

        public static readonly DependencyProperty SearchCommandProperty =
            DependencyProperty.Register( "SearchCommand", typeof( ICommand ), typeof( SearchBox ), new PropertyMetadata( null ) );
        #endregion

        #region SearchCommandSymbol DependencyProperty
        /// <summary>
        /// Symbol contained in the search button. The standard magnifying glass is the default.
        /// </summary>
        public string SearchCommandSymbol
        {
            get { return (string) GetValue( SearchCommandSymbolProperty ); }
            set { SetValue( SearchCommandSymbolProperty, value ); }
        }

        public static readonly DependencyProperty SearchCommandSymbolProperty =
            DependencyProperty.Register( "SearchCommandSymbol", typeof( string ), typeof( SearchBox ), new PropertyMetadata( "" ) );
        #endregion

        #region Query DependencyProperty
        /// <summary>
        /// The current query.
        /// </summary>
        public string Query
        {
            get { return (string) GetValue( QueryProperty ); }
            set { SetValue( QueryProperty, value ); }
        }

        public static readonly DependencyProperty QueryProperty =
            DependencyProperty.Register( "Query", typeof( string ), typeof( SearchBox ), new PropertyMetadata( "" ) );
        #endregion

        private bool _blockFocus;

        /// <summary>
        /// Ends the search.
        /// </summary>
        public Command EndSearchCommand
        {
            get { return this.GetCommand( EndSearch ); }
        }


        /// <summary>
        /// Creates a new SearchBox.
        /// </summary>
        public SearchBox()
        {
            InitializeComponent();
            LayoutRoot.DataContext = this;

            // Silly hack to ensure the focus is released when we want it to be
            Box.GotFocus += Box_GotFocus;
        }


        private void Box_GotFocus( object sender, RoutedEventArgs e )
        {
            if ( _blockFocus )
            {
                ( (PhoneApplicationPage) ( (PhoneApplicationFrame) Application.Current.RootVisual ).Content ).Focus();
            }
        }

        private void Box_SelectionChanged( object sender, SelectionChangedEventArgs e )
        {
            if ( e.AddedItems.Count > 0 )
            {
                EndSearch();
            }
        }

        private async void Box_Populating( object sender, PopulatingEventArgs e )
        {
            if ( AutoCompleteProvider == null )
            {
                return;
            }

            e.Cancel = true;
            try
            {
                Box.ItemsSource = await AutoCompleteProvider( Query );
            }
            catch
            {
                // nothing
            }

            Box.PopulateComplete();
        }

        private async void EndSearch()
        {
            if ( !string.IsNullOrWhiteSpace( Query ) && SearchCommand.CanExecute( Query ) )
            {
                _blockFocus = true;
                ( (PhoneApplicationPage) ( (PhoneApplicationFrame) Application.Current.RootVisual ).Content ).Focus();

                SearchCommand.Execute( Query );

                await Task.Delay( 500 );
                _blockFocus = false;
            }
        }
    }
}