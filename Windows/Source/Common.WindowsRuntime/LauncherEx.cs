using System;
using Windows.System;

namespace PocketCampus.Common
{
    public static class LauncherEx
    {
        public static async void Launch( string uri )
        {
            await Launcher.LaunchUriAsync( new Uri( uri, UriKind.Absolute ) );
        }
    }
}