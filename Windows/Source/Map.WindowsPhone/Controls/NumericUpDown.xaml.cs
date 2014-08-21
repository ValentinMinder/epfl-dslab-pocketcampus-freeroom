// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Windows;
using PocketCampus.Common.Controls;
using ThinMvvm;

namespace PocketCampus.Map.Controls
{
    /// <summary>
    /// Simple numeric up-down control to select a value, with a minimum and a maximum.
    /// </summary>
    public partial class NumericUpDown : ObservableControl
    {
        #region Value DependencyProperty
        /// <summary>
        /// The value.
        /// </summary>
        public int Value
        {
            get { return (int) GetValue( ValueProperty ); }
            set { SetValue( ValueProperty, value ); }
        }

        public static readonly DependencyProperty ValueProperty =
            DependencyProperty.Register( "Value", typeof( int ), typeof( NumericUpDown ), new PropertyMetadata( 0, OnValueChanged ) );

        private static void OnValueChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            var nud = (NumericUpDown) obj;
            nud.IncreaseCommand.OnCanExecuteChanged();
            nud.DecreaseCommand.OnCanExecuteChanged();
        }
        #endregion

        #region Minimum DependencyProperty
        /// <summary>
        /// The minimum value.
        /// </summary>
        public int Minimum
        {
            get { return (int) GetValue( MinimumProperty ); }
            set { SetValue( MinimumProperty, value ); }
        }

        public static readonly DependencyProperty MinimumProperty =
            DependencyProperty.Register( "Minimum", typeof( int ), typeof( NumericUpDown ),
                                         new PropertyMetadata( 0, ( o, _ ) => ( (NumericUpDown) o ).DecreaseCommand.OnCanExecuteChanged() ) );
        #endregion

        #region Maximum DependencyProperty
        /// <summary>
        /// The maximum value.
        /// </summary>
        public int Maximum
        {
            get { return (int) GetValue( MaximumProperty ); }
            set { SetValue( MaximumProperty, value ); }
        }

        public static readonly DependencyProperty MaximumProperty =
            DependencyProperty.Register( "Maximum", typeof( int ), typeof( NumericUpDown ),
                                         new PropertyMetadata( 0, ( o, _ ) => ( (NumericUpDown) o ).IncreaseCommand.OnCanExecuteChanged() ) );
        #endregion

        /// <summary>
        /// Gets the command executed to increase the value.
        /// </summary>
        public Command IncreaseCommand
        {
            get { return this.GetCommand( () => Value++, () => Value < Maximum ); }
        }

        /// <summary>
        /// Gets the command executed to decrease the value.
        /// </summary>
        public Command DecreaseCommand
        {
            get { return this.GetCommand( () => Value--, () => Value > Minimum ); }
        }


        /// <summary>
        /// Creates a new NumericUpDown.
        /// </summary>
        public NumericUpDown()
        {
            InitializeComponent();
            LayoutRoot.DataContext = this;
        }
    }
}