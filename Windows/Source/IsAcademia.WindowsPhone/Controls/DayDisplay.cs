// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Linq;
using System.Windows;
using System.Windows.Controls;
using PocketCampus.IsAcademia.ViewModels;

namespace PocketCampus.IsAcademia.Controls
{
    /// <summary>
    /// Displays periods in a day.
    /// </summary>
    public sealed class DayDisplay : UserControl
    {
        // The start and end hours of class. (the end hour is the last hour in which a period can begin, not end)
        private const int StartHour = 8, EndHour = 18;

        #region Day DependencyProperty
        /// <summary>
        /// The day to display.
        /// </summary>
        public DayInfo Day
        {
            get { return (DayInfo) GetValue( DayProperty ); }
            set { SetValue( DayProperty, value ); }
        }

        /// <summary>
        /// The day to display.
        /// </summary>
        public static readonly DependencyProperty DayProperty =
            DependencyProperty.Register( "Day", typeof( DayInfo ), typeof( DayDisplay ), new PropertyMetadata( OnDayPropertyChanged ) );

        private static void OnDayPropertyChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            ( (DayDisplay) obj ).Refresh();
        }
        #endregion

        #region PeriodTemplate DependencyProperty
        /// <summary>
        /// The template for periods.
        /// </summary>
        public DataTemplate PeriodTemplate
        {
            get { return (DataTemplate) GetValue( PeriodTemplateProperty ); }
            set { SetValue( PeriodTemplateProperty, value ); }
        }

        /// <summary>
        /// The template used for periods.
        /// </summary>
        public static readonly DependencyProperty PeriodTemplateProperty =
            DependencyProperty.Register( "PeriodTemplate", typeof( DataTemplate ), typeof( DayDisplay ), new PropertyMetadata( null ) );
        #endregion

        #region ContainerStyle DependencyProperty
        /// <summary>
        /// The style of the period containers.
        /// </summary>
        public Style ContainerStyle
        {
            get { return (Style) GetValue( ContainerStyleProperty ); }
            set { SetValue( ContainerStyleProperty, value ); }
        }

        /// <summary>
        /// The style applied to period containers.
        /// </summary>
        public static readonly DependencyProperty ContainerStyleProperty =
            DependencyProperty.Register( "ContainerStyle", typeof( Style ), typeof( DayDisplay ), new PropertyMetadata( null ) );
        #endregion

        #region EmptyContainerStyle DependencyProperty
        /// <summary>
        /// The style of "empty" period containers, i.e. containers for hours without a period.
        /// </summary>
        public Style EmptyContainerStyle
        {
            get { return (Style) GetValue( EmptyContainerStyleProperty ); }
            set { SetValue( EmptyContainerStyleProperty, value ); }
        }

        /// <summary>
        /// The style applied to empty period containers.
        /// </summary>
        public static readonly DependencyProperty EmptyContainerStyleProperty =
            DependencyProperty.Register( "EmptyContainerStyle", typeof( Style ), typeof( DayDisplay ), new PropertyMetadata( null ) );
        #endregion

        // The grid in which content is displayed
        private Grid _innerGrid;


        /// <summary>
        /// Creates a new DayDisplay.
        /// </summary>
        public DayDisplay()
        {
            Content = _innerGrid = new Grid();
        }


        /// <summary>
        /// Refresh the displayed periods.
        /// </summary>
        private void Refresh()
        {
            if ( Day == null )
            {
                return;
            }

            AddRows();
            AddColumns();
            _innerGrid.Children.Clear();
            AddPeriods();
            AddPlaceholders();
        }

        /// <summary>
        /// Add rows for each hour.
        /// </summary>
        private void AddRows()
        {
            _innerGrid.RowDefinitions.Clear();

            for ( int n = StartHour; n <= EndHour; n++ )
            {
                _innerGrid.RowDefinitions.Add( new RowDefinition() );
            }
        }

        /// <summary>
        /// Adds columns depending on the number of overlapping periods (usually there are none).
        /// </summary>
        private void AddColumns()
        {
            _innerGrid.ColumnDefinitions.Clear();

            int count = Day.Periods.Select( p => Day.Periods.Count( p2 => p.ContainedHours.Intersect( p2.ContainedHours ).Any() ) )
                                   .Aggregate( 1, LeastCommonMultiple );
            for ( int n = 0; n < count; n++ )
            {
                _innerGrid.ColumnDefinitions.Add( new ColumnDefinition() );
            }
        }

        /// <summary>
        /// Adds periods.
        /// </summary>
        private void AddPeriods()
        {
            foreach ( var period in Day.Periods )
            {
                var control = new ContentControl
                {
                    ContentTemplate = PeriodTemplate,
                    Content = period,
                    Style = ContainerStyle
                };

                int row = period.StartHour - StartHour;
                int rowSpan = period.Length;

                int columnSpan = _innerGrid.ColumnDefinitions.Count
                               / Day.Periods.Count( p => period.ContainedHours.Intersect( p.ContainedHours ).Any() );
                int column = _innerGrid.Children
                                       .Cast<FrameworkElement>()
                                       .Count( c => Enumerable.Range( Grid.GetRow( c ), Grid.GetRowSpan( c ) )
                                                              .Intersect( Enumerable.Range( row, rowSpan ) )
                                                              .Any() )
                                       * columnSpan;

                Grid.SetRow( control, row );
                Grid.SetRowSpan( control, rowSpan );

                Grid.SetColumn( control, column );
                Grid.SetColumnSpan( control, columnSpan );

                _innerGrid.Children.Add( control );
            }
        }

        /// <summary>
        /// Adds placeholders in the rows and columns that do not contain a period.
        /// </summary>
        private void AddPlaceholders()
        {
            bool[,] emptySpaces = GetEmptySpaces();

            for ( int y = 0; y < _innerGrid.RowDefinitions.Count; y++ )
            {
                Border border = null;
                int colSpan = 0;
                for ( int x = 0; x < _innerGrid.ColumnDefinitions.Count; x++ )
                {
                    if ( emptySpaces[x, y] )
                    {
                        if ( border == null )
                        {
                            border = new Border { Style = EmptyContainerStyle };
                            Grid.SetColumn( border, x );
                            Grid.SetRow( border, y );
                        }
                        colSpan++;
                    }
                    else if ( border != null )
                    {
                        Grid.SetColumnSpan( border, colSpan );
                        _innerGrid.Children.Add( border );
                        border = null;
                        colSpan = 0;
                    }
                }
                if ( border != null )
                {
                    Grid.SetColumnSpan( border, colSpan );
                    _innerGrid.Children.Add( border );
                }
            }
        }

        /// <summary>
        /// Gets an array indicating which spaces in the grid are empty.
        /// </summary>
        private bool[,] GetEmptySpaces()
        {
            bool[,] spaces = new bool[_innerGrid.ColumnDefinitions.Count, _innerGrid.RowDefinitions.Count];
            for ( int x = 0; x < spaces.GetLength( 0 ); x++ )
            {
                for ( int y = 0; y < spaces.GetLength( 1 ); y++ )
                {
                    spaces[x, y] = true;
                }
            }

            foreach ( FrameworkElement elem in _innerGrid.Children )
            {
                for ( int x = 0; x < Grid.GetColumnSpan( elem ); x++ )
                {
                    for ( int y = 0; y < Grid.GetRowSpan( elem ); y++ )
                    {
                        spaces[Grid.GetColumn( elem ) + x, Grid.GetRow( elem ) + y] = false;
                    }
                }
            }

            return spaces;
        }

        /// <summary>
        /// Finds the Least Common Multiple (LCM) of two integers.
        /// </summary>
        private static int LeastCommonMultiple( int a, int b )
        {
            int num1 = Math.Max( a, b ), num2 = Math.Min( a, b );
            for ( int i = 1; i <= num2; i++ )
            {
                if ( ( num1 * i ) % num2 == 0 )
                {
                    return i * num1;
                }
            }
            return num2;
        }
    }
}