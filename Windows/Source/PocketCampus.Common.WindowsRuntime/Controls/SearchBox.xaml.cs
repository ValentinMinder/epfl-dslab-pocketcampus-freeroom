using System.Windows.Input;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace PocketCampus.Common.Controls
{
    public sealed partial class SearchBox : UserControl
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


        public SearchBox()
        {
            InitializeComponent();
        }
    }
}