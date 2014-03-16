// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows;
using System.Windows.Controls;
using PocketCampus.IsAcademia.Models;

namespace PocketCampus.IsAcademia.Controls
{
    /// <summary>
    /// Displays one or multiple days.
    /// </summary>
    public sealed class DayDisplay : Panel
    {
        private const int HoursInDay = 24;
        private const int MinutesInHour = 60;
        private const int MinimumHoursInDay = 10;
        private const string HourFormat = @"00\:\0\0";
        private const double HoursGridWidth = 50;

        #region Day DependencyProperty
        /// <summary>
        /// The day to display, if any.
        /// </summary>
        public StudyDay Day
        {
            get { return (StudyDay) GetValue( DayProperty ); }
            set { SetValue( DayProperty, value ); }
        }

        public static readonly DependencyProperty DayProperty =
            DependencyProperty.Register( "Day", typeof( StudyDay ), typeof( DayDisplay ),
                new PropertyMetadata( ( o, _ ) => ( (Panel) o ).InvalidateArrange() ) );
        #endregion

        #region Days DependencyProperty
        /// <summary>
        /// All of the days in the week.
        /// </summary>
        public StudyDay[] Days
        {
            get { return (StudyDay[]) GetValue( DaysProperty ); }
            set { SetValue( DaysProperty, value ); }
        }

        public static readonly DependencyProperty DaysProperty =
            DependencyProperty.Register( "Days", typeof( StudyDay[] ), typeof( DayDisplay ),
                new PropertyMetadata( ( o, _ ) => ( (Panel) o ).InvalidateArrange() ) );
        #endregion

        #region HourIncrement DependencyProperty
        /// <summary>
        /// The increment in hours for the hour display.
        /// </summary>
        public int HourIncrement
        {
            get { return (int) GetValue( HourIncrementProperty ); }
            set { SetValue( HourIncrementProperty, value ); }
        }

        public static readonly DependencyProperty HourIncrementProperty =
            DependencyProperty.Register( "HourIncrement", typeof( int ), typeof( DayDisplay ), new PropertyMetadata( 1 ) );
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

        public static readonly DependencyProperty EmptyContainerStyleProperty =
            DependencyProperty.Register( "EmptyContainerStyle", typeof( Style ), typeof( DayDisplay ), new PropertyMetadata( null ) );
        #endregion

        private int _minHour;
        private int _maxHour;
        private Dictionary<UIElement, Rect> _arrangeSizes;

        /// <summary>
        /// Creates a new DayDisplay.
        /// </summary>
        public DayDisplay()
        {
            _arrangeSizes = new Dictionary<UIElement, Rect>();

            VerticalAlignment = VerticalAlignment.Stretch;
            HorizontalAlignment = HorizontalAlignment.Stretch;
        }


        /// <summary>
        /// Creates all required elements and measures them.
        /// </summary>
        protected override Size MeasureOverride( Size availableSize )
        {
            Children.Clear();
            _arrangeSizes.Clear();

            var minMax = GetHourBoundaries( Days );
            _minHour = minMax.Item1;
            _maxHour = minMax.Item2;

            var hoursGrid = CreateHoursGrid();
            Children.Add( hoursGrid );
            hoursGrid.Measure( new Size( HoursGridWidth, availableSize.Height ) );
            _arrangeSizes.Add( hoursGrid, new Rect( 0, 0, HoursGridWidth, availableSize.Height ) );

            var displayedDays = Day == null ? Days : new[] { Day };
            double canvasWidth = ( availableSize.Width - HoursGridWidth ) / displayedDays.Length;
            for ( int n = 0; n < displayedDays.Length; n++ )
            {
                var canvas = new Canvas();
                var size = new Size( canvasWidth, availableSize.Height );
                AddPlaceholdersToCanvas( canvas, size );
                AddPeriodsToCanvas( canvas, size, displayedDays[n] );
                Children.Add( canvas );
                canvas.Measure( size );
                _arrangeSizes.Add( canvas, new Rect( HoursGridWidth + canvasWidth * n, 0, canvasWidth, availableSize.Height ) );
            }

            return availableSize;
        }

        /// <summary>
        /// Arranges the elements inside the control.
        /// </summary>
        protected override Size ArrangeOverride( Size finalSize )
        {
            foreach ( var pair in _arrangeSizes )
            {
                pair.Key.Arrange( pair.Value );
            }
            return finalSize;
        }

        /// <summary>
        /// Creates a vertical grid with hours.
        /// </summary>
        private Grid CreateHoursGrid()
        {
            var grid = new Grid();

            for ( int hour = _minHour; hour < _maxHour; hour++ )
            {
                grid.RowDefinitions.Add( new RowDefinition() );

                if ( ( hour - _minHour ) % HourIncrement == 0 )
                {
                    var block = new TextBlock
                    {
                        Text = hour.ToString( HourFormat ),
                        Style = (Style) Application.Current.Resources["PhoneTextSmallStyle"],
                        VerticalAlignment = VerticalAlignment.Top,
                        HorizontalAlignment = HorizontalAlignment.Left,
                        Margin = new Thickness( 1, -5, 1, 0 )
                    };
                    Grid.SetRow( block, hour - _minHour );
                    grid.Children.Add( block );
                }
            }

            return grid;
        }

        /// <summary>
        /// Adds placeholders to the specified canvas with the specified size.
        /// </summary>
        private void AddPlaceholdersToCanvas( Canvas canvas, Size size )
        {
            int length = _maxHour - _minHour;
            double elementHeight = size.Height / length;

            for ( int row = 0; row < length; row++ )
            {
                var border = new Border
                {
                    Height = elementHeight,
                    Width = size.Width,
                    Style = EmptyContainerStyle
                };
                Canvas.SetTop( border, elementHeight * row );
                canvas.Children.Add( border );
            }
        }

        /// <summary>
        /// Adds the periods of the specified day to the specified canvas with the specified size.
        /// </summary>
        private void AddPeriodsToCanvas( Canvas canvas, Size size, StudyDay day )
        {
            var matrix = new PeriodMatrix( day );

            double heightPerMinute = size.Height / ( ( _maxHour - _minHour ) * MinutesInHour );
            double widthPerColumn = size.Width / matrix.ColumnCount;
            var startDate = day.Day.AddHours( _minHour );

            foreach ( var period in day.Periods )
            {
                var control = new ContentControl
                {
                    ContentTemplate = PeriodTemplate,
                    Content = period,
                    Style = ContainerStyle,
                    Height = ( period.End - period.Start ).TotalMinutes * heightPerMinute,
                    Width = matrix.GetWidthMultiplier( period ) * size.Width
                };

                Canvas.SetTop( control, ( period.Start - startDate ).TotalMinutes * heightPerMinute );
                Canvas.SetLeft( control, widthPerColumn * matrix.GetColumn( period ) );

                canvas.Children.Add( control );
            }
        }

        /// <summary>
        /// Gets the hour boundaries for the specified days' periods.
        /// </summary>
        private static Tuple<int, int> GetHourBoundaries( StudyDay[] days )
        {
            int min = days.Min( d => d.Periods.Any() ? d.Periods.Min( p => p.Start.Hour ) : int.MaxValue );
            int max = days.Max( d => d.Periods.Any() ? d.Periods.Max( p => HourCeiling( p.End.TimeOfDay ) ) : int.MinValue );
            if ( min + MinimumHoursInDay > HoursInDay )
            {
                min = max - MinimumHoursInDay;
            }
            else
            {
                max = Math.Max( max, min + MinimumHoursInDay );
            }
            return Tuple.Create( min, max );
        }

        /// <summary>
        /// Gets the hour ceiling for the specified time.
        /// The result is between 0 and 24 inclusive, and the method assumes the date is not 0:00.
        /// </summary>
        private static int HourCeiling( TimeSpan time )
        {
            int ceiling = (int) Math.Round( (double) time.Hours + (double) time.Minutes / (double) MinutesInHour );
            return ceiling == 0 ? 24 : ceiling;
        }


        /// <summary>
        /// Represents a matrix of periods possibly overlapping vertically.
        /// </summary>
        private sealed class PeriodMatrix
        {
            private const int HoursInDay = 24;
            private const int MinutesInHour = 60;
            private const int CollisionCheckingInterval = 15; // in minutes

            private readonly Dictionary<Period, int> _positions;
            private readonly Dictionary<Period, double> _widthMultipliers;

            public int ColumnCount { get; private set; }

            /// <summary>
            /// Creates a new PeriodMatrix for the specified day's periods.
            /// </summary>
            public PeriodMatrix( StudyDay day )
            {
                _positions = new Dictionary<Period, int>();
                _widthMultipliers = new Dictionary<Period, double>();

                ColumnCount = day.Periods
                                 .Select( p => day.Periods.Count( p2 => DoPeriodsIntersect( p, p2 ) ) )
                                 .Aggregate( 1, LeastCommonMultiple );

                int[] collisions = new int[HoursInDay * MinutesInHour / CollisionCheckingInterval];
                bool[,] used = new bool[ColumnCount, HoursInDay * MinutesInHour / CollisionCheckingInterval];

                foreach ( var period in day.Periods )
                {
                    for ( var date = period.Start; date < period.End; date = date.AddMinutes( CollisionCheckingInterval ) )
                    {
                        collisions[(int) ( date - day.Day ).TotalMinutes / CollisionCheckingInterval]++;
                    }
                }

                foreach ( var period in day.Periods )
                {
                    int totalCollisionCount = 1;
                    for ( var date = period.Start; date < period.End; date = date.AddMinutes( CollisionCheckingInterval ) )
                    {
                        totalCollisionCount = Math.Max( totalCollisionCount, collisions[(int) date.TimeOfDay.TotalMinutes / CollisionCheckingInterval] );
                    }

                    int width = ColumnCount / totalCollisionCount;

                    // this is not pretty at all
                    // it finds a column X such that if we put period at the X coordinate (its size and Y coord are known) 
                    // there are no collisions with already-placed elements
                    int startColumn = -width;
                    bool hasCollisions;
                    do
                    {
                        startColumn += width;
                        hasCollisions = false;

                        for ( int col = 0; col < width; col++ )
                        {
                            for ( var date = period.Start; date < period.End; date = date.AddMinutes( CollisionCheckingInterval ) )
                            {
                                if ( used[col + startColumn, (int) date.TimeOfDay.TotalMinutes / CollisionCheckingInterval] )
                                {
                                    hasCollisions = true;
                                    goto endloop;
                                }
                            }
                        }
                    endloop:
                        ;
                    } while ( hasCollisions );

                    for ( int col = 0; col < width; col++ )
                    {
                        for ( var date = period.Start; date < period.End; date = date.AddMinutes( CollisionCheckingInterval ) )
                        {
                            used[col + startColumn, (int) date.TimeOfDay.TotalMinutes / CollisionCheckingInterval] = true;
                        }
                    }

                    _positions.Add( period, startColumn );
                    _widthMultipliers.Add( period, 1.0 / (double) totalCollisionCount );
                }
            }

            /// <summary>
            /// Gets the column in which the specified period should be put to avoid collisions.
            /// </summary>
            public int GetColumn( Period period )
            {
                return _positions[period];
            }

            /// <summary>
            /// Gets the width multiplier for the specified period.
            /// Always less than or equal to 1.
            /// </summary>
            public double GetWidthMultiplier( Period period )
            {
                return _widthMultipliers[period];
            }


            /// <summary>
            /// Indicates whether the two specified periods intersect.
            /// </summary>
            private static bool DoPeriodsIntersect( Period p1, Period p2 )
            {
                return ( p1.Start == p2.Start && p1.End == p2.End )
                    || ( p1.Start <= p2.Start && p1.End > p2.Start )
                    || ( p1.Start >= p2.Start && p1.Start < p2.End );
            }

            /// <summary>
            /// Finds the least common multiple of the two specified integers.
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
}