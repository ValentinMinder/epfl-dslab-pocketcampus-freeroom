// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Globalization;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.Moodle.Models;
using PocketCampus.Moodle.Services;
using ThinMvvm;

namespace PocketCampus.Moodle.ViewModels
{
    public sealed class CourseViewModel : CachedDataViewModel<Course, CourseSectionsResponse>
    {
        private readonly ISecureRequestHandler _requestHandler;
        private readonly IMoodleService _moodleService;
        private readonly IMoodleDownloader _downloader;
        private readonly IFileStorage _storage;
        private readonly IBrowserService _browserService;
        private readonly Course _course;

        private CourseSection[] _sections;
        private CourseSection _selectedSection;
        private DownloadState _downloadState;


        public CourseSection[] Sections
        {
            get { return _sections; }
            private set { SetProperty( ref _sections, value ); }
        }

        public CourseSection SelectedSection
        {
            get { return _selectedSection; }
            private set { SetProperty( ref _selectedSection, value ); }
        }

        /// <summary>
        /// Gets the state of the current download (or lack thereof).
        /// </summary>
        public DownloadState DownloadState
        {
            get { return _downloadState; }
            private set { SetProperty( ref _downloadState, value ); }
        }


        /// <summary>
        /// Gets the command executed to download and open a file.
        /// </summary>
        public AsyncCommand<MoodleFile> DownloadAndOpenCommand
        {
            get { return this.GetAsyncCommand<MoodleFile>( DownloadAndOpenAsync ); }
        }

        /// <summary>
        /// Gets the command executed to open a link.
        /// </summary>
        public Command<MoodleLink> OpenLinkCommand
        {
            get { return this.GetCommand<MoodleLink>( l => _browserService.NavigateTo( l.Url ) ); }
        }


        public CourseViewModel( IDataCache cache, ISecureRequestHandler requestHandler, IMoodleService moodleService,
                                IMoodleDownloader downloader, IFileStorage storage, IBrowserService browserService,
                                Course course )
            : base( cache )
        {
            _requestHandler = requestHandler;
            _moodleService = moodleService;
            _downloader = downloader;
            _storage = storage;
            _browserService = browserService;
            _course = course;
        }

        protected override CachedTask<CourseSectionsResponse> GetData( bool force, CancellationToken token )
        {
            if ( !force )
            {
                return CachedTask.NoNewData<CourseSectionsResponse>();
            }

            return CachedTask.Create( () => _requestHandler.ExecuteAsync( () =>
            {
                var request = new CourseSectionsRequest
                {
                    Language = CultureInfo.CurrentCulture.TwoLetterISOLanguageName,
                    CourseId = _course.Id
                };
                return _moodleService.GetSectionsAsync( request, token );
            } ), _course.Id );
        }

        protected override bool HandleData( CourseSectionsResponse data, CancellationToken token )
        {
            if ( data.Status == MoodleStatus.AuthenticationError )
            {
                _requestHandler.Authenticate<MainViewModel>();
                return false;
            }
            if ( data.Status != MoodleStatus.Success )
            {
                throw new Exception( "An error occurred on the server while fetching sections." );
            }

            if ( !token.IsCancellationRequested )
            {
                // set the files' paths
                foreach ( var section in data.Sections )
                {
                    foreach ( var resource in section.Resources )
                    {
                        if ( resource.File != null )
                        {
                            resource.File.PathComponents = new[] { _course.Name, section.DisplayTitle };
                        }
                        if ( resource.Folder != null )
                        {
                            foreach ( var file in resource.Folder.Files )
                            {
                                file.PathComponents = new[] { _course.Name, section.DisplayTitle, resource.Folder.Name };
                            }
                        }
                    }
                }

                Sections = data.Sections;
                SelectedSection = Sections.FirstOrDefault( s => s.Title == null && s.StartDate.Value <= DateTime.Now && DateTime.Now <= s.EndDate.Value )
                               ?? Sections.FirstOrDefault();
            }

            return true;
        }


        /// <summary>
        /// Downloads (if it hasn't already been downloaded) and opens the specified file.
        /// </summary>
        private async Task DownloadAndOpenAsync( MoodleFile file )
        {
            if ( DownloadState == DownloadState.Downloading )
            {
                return;
            }

            if ( !( await _storage.IsStoredAsync( file ) ) )
            {
                DownloadState = DownloadState.Downloading;

                try
                {
                    var bytes = await _downloader.DownloadAsync( file );
                    await _storage.StoreFileAsync( file, bytes );
                    DownloadState = DownloadState.None;
                }
                catch
                {
                    DownloadState = DownloadState.Error;
                }
            }
            if ( DownloadState == DownloadState.None )
            {
                await _storage.OpenFileAsync( file );
            }
        }
    }
}