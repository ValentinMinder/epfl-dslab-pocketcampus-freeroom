using PocketCampus.Authentication.ViewModels;
using ThinMvvm;

namespace PocketCampus.Authentication
{
    public static class INavigationServiceExtensions
    {
        public static void ForceAuthentication<TViewModel>( this INavigationService navigationService )
            where TViewModel : ViewModel<NoParameter>
        {
            navigationService.RemoveCurrentFromBackStack();
            navigationService.NavigateTo<MainViewModel, AuthenticationRequest>( new AuthenticationRequest( () => navigationService.NavigateTo<TViewModel>() ) );
        }

        public static void ForceAuthentication<TViewModel, TParameter>( this INavigationService navigationService, TParameter parameter )
            where TViewModel : ViewModel<TParameter>
        {
            navigationService.RemoveCurrentFromBackStack();
            navigationService.NavigateTo<MainViewModel, AuthenticationRequest>( new AuthenticationRequest( () => navigationService.NavigateTo<TViewModel, TParameter>( parameter ) ) );
        }
    }
}