// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.IO;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.Moodle.Models;
using PocketCampus.Moodle.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Moodle.ViewModels
{
    /// <summary>
    /// The main (and only) ViewModel.
    /// </summary>
    [LogId( "/moodle" )]
    public sealed class MainViewModel : DataViewModel<NoParameter>
    {
        private const char UrlParametersPrefix = '?';

        private readonly ISecureRequestHandler _requestHandler;
        private readonly IMoodleService _moodleService;
        private readonly IMoodleDownloader _downloader;
        private readonly IFileStorage _storage;

        private Course[] _courses;
        private bool _anyCourses;
        private DownloadState _downloadState;

        /// <summary>
        /// Gets the courses the user is enrolled in.
        /// </summary>
        public Course[] Courses
        {
            get { return _courses; }
            private set { SetProperty( ref _courses, value ); }
        }

        /// <summary>
        /// Gets a value indicating whether there are any courses.
        /// </summary>
        public bool AnyCourses
        {
            get { return _anyCourses; }
            private set { SetProperty( ref _anyCourses, value ); }
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
        [LogId( "DownloadAndOpenFile" )]
        public AsyncCommand<CourseFile> DownloadAndOpenCommand
        {
            get { return GetAsyncCommand<CourseFile>( DownloadAndOpenAsync ); }
        }

        /// <summary>
        /// Creates a new MainViewModel.
        /// </summary>
        public MainViewModel( ISecureRequestHandler requestHandler, IMoodleService moodleService,
                              IMoodleDownloader downloader, IFileStorage storage )
        {
            _requestHandler = requestHandler;
            _moodleService = moodleService;
            _downloader = downloader;
            _storage = storage;
        }

        /// <summary>
        /// Downloads (if it hasn't already been downloaded) and opens the specified file.
        /// </summary>
        private async Task DownloadAndOpenAsync( CourseFile file )
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
                    var bytes = await _downloader.DownloadAsync( file.Url );
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

        /// <summary>
        /// Refreshes the data.
        /// </summary>
        protected override Task RefreshAsync( CancellationToken token, bool force )
        {
            return _requestHandler.ExecuteAsync<MainViewModel>( async () =>
            {
                if ( !force )
                {
                    return true;
                }

                var coursesResponse = await _moodleService.GetCoursesAsync( "dummy" );

                if ( coursesResponse.Status == ResponseStatus.AuthenticationError )
                {
                    return false;
                }
                if ( coursesResponse.Status != ResponseStatus.Success )
                {
                    throw new Exception( "An error occurred on the server while fetching the coursed." );
                }

                foreach ( var course in coursesResponse.Courses )
                {
                    var sectionsResponse = await _moodleService.GetCourseSectionsAsync( course.Id.ToString() );

                    if ( sectionsResponse.Status == ResponseStatus.AuthenticationError )
                    {
                        return false;
                    }
                    if ( sectionsResponse.Status != ResponseStatus.Success )
                    {
                        throw new Exception( "An error occurred on the server while fetching a course's sections." );
                    }

                    course.Sections = sectionsResponse.Sections.Where( s => s.Files.Length > 0 ).ToArray();

                    foreach ( var section in course.Sections )
                    {
                        foreach ( var file in section.Files )
                        {
                            // This is used to know where to store files
                            file.Course = course;
                            // The file names don't have extensions so we must use the one from their URL
                            // but also remove the URL parameters because Path.GetExtension obviously doesn't do it
                            file.Name = Path.ChangeExtension( file.Name, Path.GetExtension( file.Url.Split( UrlParametersPrefix )[0] ) );
                        }
                    }
                }

                if ( !token.IsCancellationRequested )
                {
                    Courses = coursesResponse.Courses.Where( c => c.Sections.Length > 0 ).ToArray();
                    AnyCourses = Courses.Length > 0;
                }

                return true;
            } );
        }
    }
}