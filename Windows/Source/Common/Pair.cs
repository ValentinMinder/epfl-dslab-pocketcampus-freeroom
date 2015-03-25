// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Diagnostics;
using System.Runtime.Serialization;
using ThinMvvm;

namespace PocketCampus.Common
{
    /// <summary>
    /// A mutable and observable tuple.
    /// </summary>
    /// <remarks>
    /// The DataContract and DataMember attributes are required for settings serialization.
    /// </remarks>
    [DebuggerDisplay( "{Item1}, {Item2}" )]
    [DataContract]
    public sealed class Pair<T1, T2> : ObservableObject
    {
        private T1 _item1;
        private T2 _item2;

        /// <summary>
        /// Gets or sets the first item.
        /// </summary>
        [DataMember]
        public T1 Item1
        {
            get { return _item1; }
            set { SetProperty( ref _item1, value ); }
        }

        /// <summary>
        /// Gets or sets the second item.
        /// </summary>
        [DataMember]
        public T2 Item2
        {
            get { return _item2; }
            set { SetProperty( ref _item2, value ); }
        }


        /// <summary>
        /// Clones the pair. This performs a shallow copy.
        /// </summary>
        public Pair<T1, T2> Clone()
        {
            return Pair.Create( Item1, Item2 );
        }
    }

    /// <summary>
    /// Utility class for type inference.
    /// </summary>
    public static class Pair
    {
        /// <summary>
        /// Creates a new Pair containing the specified objects.
        /// </summary>
        public static Pair<T1, T2> Create<T1, T2>( T1 item1, T2 item2 )
        {
            return new Pair<T1, T2> { Item1 = item1, Item2 = item2 };
        }
    }
}