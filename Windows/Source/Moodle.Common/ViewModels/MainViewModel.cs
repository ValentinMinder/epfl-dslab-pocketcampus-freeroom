// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Common.Services;
using PocketCampus.Moodle.Models;
using PocketCampus.Moodle.Services;
using PocketCampus.Mvvm;
using PocketCampus.Mvvm.Logging;

namespace PocketCampus.Moodle.ViewModels
{
    /// <summary>
    /// The main (and only) ViewModel.
    /// </summary>
    [PageLogId( "/moodle" )]
    public sealed class MainViewModel : DataViewModel<NoParameter>
    {
        private readonly IMoodleService _moodleService;
        private readonly ISecureRequestHandler _requestHandler;
        private readonly IMoodleDownloader _downloader;
        private readonly IFileStorage _storage;

        private Course[] _courses;
        private bool _anyCourses;
        private DownloadState _downloadState;

        // This is not optimal, but it's extremely unlikely that an user would stay enough time here to invalidate the cookie
        private string _moodleCookie;

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
        [CommandLogId( "DownloadAndOpenFile" )]
        public AsyncCommand<CourseFile> DownloadAndOpenCommand
        {
            get { return GetAsyncCommand<CourseFile>( DownloadAndOpenAsync ); }
        }

        /// <summary>
        /// Creates a new MainViewModel.
        /// </summary>
        public MainViewModel( IMoodleService moodleService, ISecureRequestHandler requestHandler,
                              IMoodleDownloader downloader, IFileStorage storage )
        {
            _moodleService = moodleService;
            _requestHandler = requestHandler;
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
                    var bytes = await _downloader.DownloadAsync( file.Url, _moodleCookie );
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
            return _requestHandler.ExecuteAsync<MainViewModel, TequilaToken, MoodleSession>( _moodleService, async session =>
            {
                _moodleCookie = session.Cookie;

                if ( !force )
                {
                    return true;
                }

                var sessionId = new SessionId { Cookie = session.Cookie };
                var coursesRequest = new MoodleRequest
                {
                    Language = CultureInfo.CurrentUICulture.TwoLetterISOLanguageName,
                    SessionId = sessionId
                };
                var coursesResponse = await _moodleService.GetCoursesAsync( coursesRequest );

                if ( coursesResponse.Status == ResponseStatus.AuthenticationError )
                {
                    return false;
                }
                if ( coursesResponse.Status != ResponseStatus.Ok )
                {
                    throw new Exception( "An error occurred on the server while fetching the coursed." );
                }

                foreach ( var course in coursesResponse.Courses )
                {
                    var sectionsRequest = new MoodleRequest
                    {
                        Language = CultureInfo.CurrentUICulture.TwoLetterISOLanguageName,
                        SessionId = sessionId,
                        CourseId = course.Id
                    };
                    var sectionsResponse = await _moodleService.GetCourseSectionsAsync( sectionsRequest );

                    if ( sectionsResponse.Status == ResponseStatus.AuthenticationError )
                    {
                        return false;
                    }
                    else if ( sectionsResponse.Status != ResponseStatus.Ok )
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
                            // The file names don't have extensions :/
                            file.Name = Path.ChangeExtension( file.Name, Path.GetExtension( file.Url ) );
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