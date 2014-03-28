// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using ThinMvvm;

namespace PocketCampus.Events
{
    /// <summary>
    /// Filter with a display name and an ID.
    /// </summary>
    /// <typeparam name="T"></typeparam>
    public sealed class Filter<T> : ObservableObject
    {
        private bool _include;


        /// <summary>
        /// Gets the filter's display name.
        /// </summary>
        public string DisplayName { get; private set; }

        /// <summary>
        /// Gets the filter's ID.
        /// </summary>
        public T Id { get; private set; }

        /// <summary>
        /// Gets a value indicating whether the associated item should be included.
        /// </summary>
        public bool Include
        {
            get { return _include; }
            set { SetProperty( ref _include, value ); }
        }


        /// <summary>
        /// Creates a new Filter.
        /// </summary>
        public Filter( string displayName, T id, bool include )
        {
            DisplayName = displayName;
            Id = id;
            Include = include;
        }
    }
}