// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows;
using System.Windows.Navigation;
using Microsoft.Phone.Controls;
using PocketCampus.Common.Services;
using PocketCampus.Mvvm;
using PocketCampus.Mvvm.Logging;

namespace PocketCampus.Main.Services
{
    /// <summary>
    /// Implementation of IWindowsPhoneNavigationService.
    /// </summary>
    public sealed class WindowsPhoneNavigationService : IWindowsPhoneNavigationService
    {
        private const char UriParametersPrefix = '?';
        private const char UriParametersDelimiter = '&';
        private const string UniqueParameter = "mvvm_unique_id";

        private readonly NavigationLogger _logger;
        private readonly Dictionary<Type, Uri> _views;
        // HACK: IViewModel can't be covariant to be used with value types
        //       and having a non-generic IViewModel that shouldn't be implemented is a terrible idea
        //       so we use dynamic to call OnNavigatedTo/From
        private readonly Stack<dynamic> _backStack;
        private readonly Stack<bool> _ignored;

        private bool _removeCurrentFromBackstack;

        private PhoneApplicationFrame _rootFrame
        {
            get { return (PhoneApplicationFrame) Application.Current.RootVisual; }
        }


        /// <summary>
        /// Creates a new FrameNavigationService.
        /// </summary>
        public WindowsPhoneNavigationService( NavigationLogger logger )
        {
            _logger = logger;
            _views = new Dictionary<Type, Uri>();
            _backStack = new Stack<dynamic>();
            _ignored = new Stack<bool>();

            _rootFrame.Navigated += Frame_Navigated;
        }


        /// <summary>
        /// Navigates to the specified ViewModel.
        /// </summary>
        private void NavigateToPrivate( object viewModel )
        {
            var viewModelType = viewModel.GetType();
            _logger.LogNavigation( viewModel, true );
            _backStack.Push( viewModel );
            _rootFrame.Navigate( MakeUnique( _views[viewModelType] ) );
        }

        /// <summary>
        /// Occurs when the frame has navigated, either because the user requested it or because the program did.
        /// </summary>
        private void Frame_Navigated( object sender, NavigationEventArgs e )
        {
            var page = (PhoneApplicationPage) _rootFrame.Content;

            // need to check IsNavigationInitiator to avoid doing stuff when the user
            // long-presses the Back button to multitask
            if ( e.NavigationMode == NavigationMode.Back && e.IsNavigationInitiator )
            {
                if ( _ignored.Pop() )
                {
                    return;
                }

                if ( _backStack.Count > 0 )
                {
                    var currentTop = _backStack.Pop();
                    currentTop.OnNavigatedFrom();
                    DisposeIfNeeded( currentTop );
                }
                if ( _backStack.Count > 0 )
                {
                    var currentViewModel = _backStack.Peek();
                    currentViewModel.OnNavigatedTo();
                    page.DataContext = currentViewModel;
                    _logger.LogNavigation( currentViewModel, false );
                }
            }
            else if ( e.NavigationMode == NavigationMode.Forward || e.NavigationMode == NavigationMode.New )
            {
                // Ignore pages we don't know about
                if ( !_views.Any( p => UriEquals( p.Value, e.Uri ) ) )
                {
                    _ignored.Push( true );
                    return;
                }
                _ignored.Push( false );

                if ( _removeCurrentFromBackstack )
                {
                    _rootFrame.RemoveBackEntry();

                    var newTop = _backStack.Pop();
                    var currentTop = _backStack.Pop();
                    _backStack.Push( newTop );

                    DisposeIfNeeded( currentTop );

                    _removeCurrentFromBackstack = false;
                }

                if ( _backStack.Count > 0 )
                {
                    var currentViewModel = _backStack.Peek();
                    currentViewModel.OnNavigatedTo();
                    page.DataContext = currentViewModel;
                    _logger.LogNavigation( currentViewModel, false );
                }
            }
        }

        /// <summary>
        /// Disposes of the specified object, if it is an IDisposable.
        /// </summary>
        private static void DisposeIfNeeded( object obj )
        {
            var disposable = obj as IDisposable;
            if ( disposable != null )
            {
                disposable.Dispose();
            }
        }

        /// <summary>
        /// Makes a unique URI out of the specified URI.
        /// </summary>
        /// <remarks>
        /// This allows same-page navigations.
        /// </remarks>
        private static Uri MakeUnique( Uri uri )
        {
            string uniqueParameterValue = Guid.NewGuid().ToString();
            char separator = uri.ToString().Contains( UriParametersPrefix ) ? UriParametersDelimiter
                                                                                   : UriParametersPrefix;
            return new Uri( uri.ToString() + separator + UniqueParameter + uniqueParameterValue, UriKind.RelativeOrAbsolute );
        }

        /// <summary>
        /// Indicates whether the two specified URIs are considered to be equal.
        /// </summary>
        private static bool UriEquals( Uri uri1, Uri uri2 )
        {
            return GetUriPath( uri1.ToString() ) == GetUriPath( uri2.ToString() );
        }

        /// <summary>
        /// Gets the path section of the specified URI.
        /// </summary>
        private static string GetUriPath( string uri )
        {
            if ( uri.Contains( UriParametersPrefix ) )
            {
                return uri.ToString().Substring( 0, uri.ToString().IndexOf( UriParametersPrefix ) );
            }
            return uri;
        }

        #region INavigationService implementation
        /// <summary>
        /// If no dialog is present on screen, navigates to a ViewModel of the specified type.
        /// Otherwise, does so after (and if) the current dialog is exited successfully.
        /// </summary>
        public void NavigateTo<T>()
            where T : IViewModel<NoParameter>
        {
            var vmType = typeof( T );
            var vm = Container.Get( vmType, null );
            NavigateToPrivate( vm );
        }

        /// <summary>
        /// If no dialog is present on screen, navigates to a ViewModel of the specified type.
        /// Otherwise, does so after (and if) the current dialog is exited successfully.
        /// </summary>
        public void NavigateTo<TViewModel, TArg>( TArg arg )
            where TViewModel : IViewModel<TArg>
        {
            var vmType = typeof( TViewModel );
            var vm = Container.Get( vmType, arg );
            NavigateToPrivate( vm );
        }

        /// <summary>
        /// If no dialog was present, goes back to the previous ViewModel.
        /// Otherwise, goes forward. (this allows dialogs to be used as normal VMs)
        /// </summary>
        public void NavigateBack()
        {
            if ( _backStack.Count > 0 )
            {
                _rootFrame.GoBack();
            }
            else
            {
                Application.Current.Terminate();
            }
        }

        /// <summary>
        /// Pops the back-stack used by this navigation service.
        /// Used to ignore the current ViewModel when going back.
        /// </summary>
        public void PopBackStack()
        {
            _removeCurrentFromBackstack = true;
        }
        #endregion

        #region IWindowsPhoneNavigationService implementation
        /// <summary>
        /// Adds a ViewModel to View URI link.
        /// </summary>
        /// <typeparam name="TViewModel">The ViewModel's type.</typeparam>
        /// <param name="viewUri">The View URI. Needs to be relative to the app root (e.g. /MyApp;Component/Views/MyView.xaml).</param>
        public void Bind<TViewModel>( string viewUri )
        {
            _views.Add( typeof( TViewModel ), new Uri( viewUri, UriKind.Relative ) );
        }
        #endregion
    }
}