using System;
using Windows.UI.Xaml.Data;

namespace PocketCampus.Common
{
    public sealed class StringFormatConverter : IValueConverter
    {
        public object Convert( object value, Type targetType, object parameter, string language )
        {
            return string.Format( parameter.ToString(), value );
        }

        public object ConvertBack( object value, Type targetType, object parameter, string language )
        {
            throw new NotSupportedException();
        }
    }
}