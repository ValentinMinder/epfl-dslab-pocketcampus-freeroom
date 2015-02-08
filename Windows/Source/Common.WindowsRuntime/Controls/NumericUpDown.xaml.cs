// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using ThinMvvm;
using Windows.UI.Xaml;

namespace PocketCampus.Common.Controls
{
    public sealed partial class NumericUpDown : ICommandOwner
    {
        #region Minimum
        public int Minimum
        {
            get { return (int) GetValue( MinimumProperty ); }
            set { SetValue( MinimumProperty, value ); }
        }

        public static readonly DependencyProperty MinimumProperty =
            DependencyProperty.Register( "Minimum", typeof( int ), typeof( NumericUpDown ), new PropertyMetadata( int.MinValue, OnPropertyChanged ) );
        #endregion

        #region Maximum
        public int Maximum
        {
            get { return (int) GetValue( MaximumProperty ); }
            set { SetValue( MaximumProperty, value ); }
        }

        public static readonly DependencyProperty MaximumProperty =
            DependencyProperty.Register( "Maximum", typeof( int ), typeof( NumericUpDown ), new PropertyMetadata( int.MaxValue, OnPropertyChanged ) );
        #endregion

        #region Value
        public int Value
        {
            get { return (int) GetValue( ValueProperty ); }
            set { SetValue( ValueProperty, value ); }
        }

        public static readonly DependencyProperty ValueProperty =
            DependencyProperty.Register( "Value", typeof( int ), typeof( NumericUpDown ), new PropertyMetadata( 0, OnPropertyChanged ) );
        #endregion

        public Command IncrementCommand
        {
            get { return this.GetCommand( () => Value++, () => Value < Maximum ); }
        }

        public Command DecrementCommand
        {
            get { return this.GetCommand( () => Value--, () => Value > Minimum ); }
        }

        public NumericUpDown()
        {
            InitializeComponent();
            Root.DataContext = this;
            // HACK: For some reason a binding on this doesn't work; why?
            Loaded += ( _, __ ) => Root.Background = Background;
        }

        private static void OnPropertyChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            var control = (NumericUpDown) obj;
            control.IncrementCommand.OnCanExecuteChanged();
            control.DecrementCommand.OnCanExecuteChanged();
        }
    }
}