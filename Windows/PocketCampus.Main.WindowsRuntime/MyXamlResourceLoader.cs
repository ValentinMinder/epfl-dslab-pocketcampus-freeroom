using PocketCampus.Common;
using Windows.UI.Xaml.Resources;

namespace PocketCampus.Main
{
    // Brilliant and awful hack to improve localization support; no more X.Content or Y.Title and x:Uid not working
    public sealed class MyXamlResourceLoader : CustomXamlResourceLoader
    {
        protected override object GetResource( string resourceId, string objectType, string propertyName, string propertyType )
        {
            var parts = resourceId.Split( '.' );
            return LocalizationHelper.GetLoaderForCurrentAssembly( parts[0] ).GetString( parts[1] );
        }
    }
}