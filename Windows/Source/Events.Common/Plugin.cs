using PocketCampus.Common;
using PocketCampus.Events.Models;
using PocketCampus.Events.Services;
using PocketCampus.Events.ViewModels;
using PocketCampus.Mvvm;

namespace PocketCampus.Events
{
    public sealed class Plugin : IPlugin
    {
        public string Id
        {
            get { return "Events"; }
        }

        public bool RequiresAuthentication
        {
            get { return false; }
        }

        public void Initialize( INavigationService navigationService )
        {
            Container.Bind<IEventsService, EventsService>();
        }

        public void NavigateTo( INavigationService navigationService )
        {
            navigationService.NavigateTo<EventPoolViewModel, long>( EventPool.RootId );
        }
    }
}