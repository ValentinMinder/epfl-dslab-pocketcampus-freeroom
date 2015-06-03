// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Linq;
using PocketCampus.IsAcademia.Models;
using Windows.Foundation;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

// TODO: The algorithm used to detect collisions is brittle and untested... test/clean/fix it.

namespace PocketCampus.IsAcademia.Controls
{
    public sealed class DayDisplay : Panel
    {
        private const int HoursInDay = 24;
        private const int MinutesInHour = 60;
        private const int MinimumHoursInDay = 10;
        private const string HourFormat = @"00\:\0\0";
        private const double HoursGridWidth = 50;
        private const int EmptyScheduleStart = 8;
        private const int EmptyScheduleEnd = 19;

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
                new PropertyMetadata( null, ( o, _ ) => ( (Panel) o ).InvalidateMeasure() ) );
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
                new PropertyMetadata( null, ( o, _ ) => ( (Panel) o ).InvalidateMeasure() ) );
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

        private readonly Dictionary<UIElement, Rect> _arrangeSizes;
        private int _minHour;
        private int _maxHour;


        public DayDisplay()
        {
            _arrangeSizes = new Dictionary<UIElement, Rect>();

            VerticalAlignment = VerticalAlignment.Stretch;
            HorizontalAlignment = HorizontalAlignment.Stretch;
        }


        protected override Size MeasureOverride( Size availableSize )
        {
            if ( Days == null )
            {
                return availableSize;
            }

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
                _arrangeSizes.Add( canvas, new Rect( Math.Floor( HoursGridWidth + canvasWidth * n ), 0, canvasWidth, availableSize.Height ) );
            }

            return availableSize;
        }


        protected override Size ArrangeOverride( Size finalSize )
        {
            foreach ( var pair in _arrangeSizes )
            {
                pair.Key.Arrange( pair.Value );
            }
            return finalSize;
        }


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
                        Style = (Style) Application.Current.Resources["AppSubtleTextBlockStyle"],
                        VerticalAlignment = VerticalAlignment.Top,
                        HorizontalAlignment = HorizontalAlignment.Left,
                        Margin = new Thickness( 0, -4, 0, 0 )
                    };
                    Grid.SetRow( block, hour - _minHour );
                    grid.Children.Add( block );
                }
            }

            return grid;
        }


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


        private static Tuple<int, int> GetHourBoundaries( StudyDay[] days )
        {
            int min = days.Min( d => d.Periods.Any() ? d.Periods.Min( p => p.Start.Hour ) : EmptyScheduleStart );
            int max = days.Max( d => d.Periods.Any() ? d.Periods.Max( p => HourCeiling( p.End.TimeOfDay ) ) : EmptyScheduleEnd );
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

        private static int HourCeiling( TimeSpan time )
        {
            int ceiling = (int) Math.Round( (double) time.Hours + (double) time.Minutes / (double) MinutesInHour );
            return ceiling == 0 ? 24 : ceiling;
        }


        private sealed class PeriodMatrix
        {
            private const int CollisionCheckingInterval = 15; // in minutes

            private readonly Dictionary<Period, int> _positions;
            private readonly Dictionary<Period, double> _widthMultipliers;

            public int ColumnCount { get; private set; }


            public PeriodMatrix( StudyDay day )
            {
                _positions = new Dictionary<Period, int>();
                _widthMultipliers = new Dictionary<Period, double>();

                int[] periodsPerInterval = new int[HoursInDay * MinutesInHour / CollisionCheckingInterval];

                foreach ( var period in day.Periods )
                {
                    IterateOverIntervals( period, n => periodsPerInterval[n]++ );
                }

                ColumnCount = periodsPerInterval.Max();
                int[] remainingPeriods = (int[]) periodsPerInterval.Clone();

                // First order the periods by number of periods that collide, otherwise the algorithm fails.
                // e.g.
                //       ---------
                // --------  -----
                // if we don't handle the top one first, both it and the first bottom one will be in column 1.
                var totalCollisionsPerPeriods =
                    day.Periods.ToDictionary( p => p,
                        p => day.Periods.Count( p2 =>
                            ( p2.Start >= p.Start && p2.Start < p.End ) // start is inside the period
                            || ( p2.End >= p.Start && p2.End < p.End ) // end is inside the period
                            || ( p2.Start <= p.Start && p2.End >= p.End ) // start and end surround the period
                            ) );

                foreach ( var pair in totalCollisionsPerPeriods.OrderByDescending( p => p.Value ) )
                {
                    var period = pair.Key;
                    int collisions = 1;
                    int column = 0;

                    IterateOverIntervals( period, n => collisions = Math.Max( collisions, periodsPerInterval[n] ) );
                    IterateOverIntervals( period, n => { remainingPeriods[n]--; column = Math.Max( column, remainingPeriods[n] ); } );

                    _positions.Add( period, column );
                    _widthMultipliers.Add( period, 1.0 / collisions );
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
            /// Iterates over the specified period, executing the specified action that takes an index.
            /// </summary>
            private static void IterateOverIntervals( Period period, Action<int> action )
            {
                for ( var date = period.Start; date < period.End; date = date.AddMinutes( CollisionCheckingInterval ) )
                {
                    action( (int) date.TimeOfDay.TotalMinutes / CollisionCheckingInterval );
                }
            }
        }
    }
}