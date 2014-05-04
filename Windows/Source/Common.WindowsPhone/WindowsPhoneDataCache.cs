using System;
using System.Collections.Generic;
using System.IO.IsolatedStorage;
using System.Linq;
using System.Runtime.Serialization;

namespace PocketCampus.Common
{
    /// <summary>
    /// IDataCache implementation for Windows Phone, using isolated storage settings.
    /// </summary>
    public sealed class WindowsPhoneDataCache : IDataCache
    {
        private const string MetadataKey = "ThinMvvm.WindowsPhone.CacheMetadata";
        private const string DataKeyFormat = "ThinMvvm.WindowsPhone.Cache_{0}_{1}";

        private readonly IsolatedStorageSettings _settings;
        private Dictionary<string, object> _data;
        private CacheMetadata _metadata;


        /// <summary>
        /// Creates a new instance of WindowsPhoneDataCache.
        /// </summary>
        public WindowsPhoneDataCache()
        {
            _settings = IsolatedStorageSettings.ApplicationSettings;
            _metadata = LoadMetadata( _settings );
            _data = LoadData( _metadata, _settings );

            Cleanup();
        }


        /// <summary>
        /// Attempts to get the value stored by the specified owner type, with the specified ID.
        /// </summary>
        /// <typeparam name="T">The value type.</typeparam>
        /// <param name="owner">The owner type.</param>
        /// <param name="id">The ID.</param>
        /// <param name="value">The value, if any.</param>
        /// <returns>A value indicating whether a value was found.</returns>
        public bool TryGet<T>( Type owner, long id, out T value )
        {
            string key = GetKey( owner.FullName, id );
            bool? upToDate = _metadata.IsUpToDate( owner.FullName, id );

            if ( upToDate == true )
            {
                value = (T) _data[key];
                return true;
            }
            else if ( upToDate == null )
            {
                _metadata.Remove( key, id );
            }

            value = default( T );
            return false;
        }

        /// <summary>
        /// Sets the specified value for the specified owner type, with the specified ID.
        /// </summary>
        /// <param name="owner">The owner type.</param>
        /// <param name="id">The ID.</param>
        /// <param name="expirationDate">The expiration date.</param>
        /// <param name="value">The value.</param>
        public void Set( Type owner, long id, DateTime expirationDate, object value )
        {
            _data[GetKey( owner.FullName, id )] = value;
            _metadata.Add( owner.FullName, id, expirationDate );

            SaveMetadata( _metadata, _settings );
            SaveData( _data, _settings );
        }


        /// <summary>
        /// Removes the values stored by the specified owner type, with the specified ID.
        /// </summary>
        private void Remove( Type owner, long id )
        {
            _data.Remove( GetKey( owner.FullName, id ) );
            _metadata.Remove( owner.FullName, id );
        }

        /// <summary>
        /// Cleans up the old values in the cache.
        /// </summary>
        private void Cleanup()
        {
            foreach ( var tup in _metadata.Cleanup() )
            {
                _data.Remove( GetKey( tup.Item1, tup.Item2 ) );
            }
            SaveData( _data, _settings );
            SaveMetadata( _metadata, _settings );
        }


        /// <summary>
        /// Loads the metadata from isolated storage.
        /// </summary>
        private static CacheMetadata LoadMetadata( IsolatedStorageSettings settings )
        {
            CacheMetadata metadata;
            if ( !settings.TryGetValue( MetadataKey, out metadata ) )
            {
                metadata = new CacheMetadata();
            }
            return metadata;
        }

        /// <summary>
        /// Loads the data from isolated storage.
        /// </summary>
        private static Dictionary<string, object> LoadData( CacheMetadata metadata, IsolatedStorageSettings settings )
        {
            var data = new Dictionary<string, object>();

            foreach ( string key in metadata.Data.SelectMany( p1 => p1.Value.Select( p2 => GetKey( p1.Key, p2.Key ) ) ) )
            {
                data[key] = settings[key];
            }

            return data;
        }

        /// <summary>
        /// Saves the metadata to isolated storage.
        /// </summary>
        private static void SaveMetadata( CacheMetadata metadata, IsolatedStorageSettings settings )
        {
            settings[MetadataKey] = metadata;
            settings.Save();
        }

        /// <summary>
        /// Saves the data to isolated storage.
        /// </summary>
        private static void SaveData( Dictionary<string, object> data, IsolatedStorageSettings settings )
        {
            foreach ( var pair in data )
            {
                settings[pair.Key] = pair.Value;
            }

            settings.Save();
        }

        /// <summary>
        /// Gets the setting key associated with the specified key and ID.
        /// </summary>
        private static string GetKey( string key, long id )
        {
            return string.Format( DataKeyFormat, key, id );
        }


        /// <summary>
        /// Serializable etadata for the cache.
        /// </summary>
        [DataContract]
        private sealed class CacheMetadata
        {
            /// <summary>
            /// The serialized data.
            /// </summary>
            [DataMember]
            public Dictionary<string, Dictionary<long, DateTime>> Data { get; set; }


            /// <summary>
            /// Creates a new instance of CacheMetadata.
            /// </summary>
            public CacheMetadata()
            {
                Data = new Dictionary<string, Dictionary<long, DateTime>>();
            }

            /// <summary>
            /// Adds the specified expiration date, associated with the specified key and ID, in the metadata.
            /// </summary>
            public void Add( string key, long id, DateTime expirationDate )
            {
                if ( !Data.ContainsKey( key ) )
                {
                    Data.Add( key, new Dictionary<long, DateTime>() );
                }
                Data[key][id] = expirationDate;
            }

            /// <summary>
            /// Removes the specified key and ID from the metadata.
            /// </summary>
            public void Remove( string key, long id )
            {
                if ( Data.ContainsKey( key ) )
                {
                    Data[key].Remove( id );
                    if ( Data[key].Count == 0 )
                    {
                        Data.Remove( key );
                    }
                }
            }

            /// <summary>
            /// Gets a value indicating whether the data associated with the specified key and ID is up to date, or null if it's not present.
            /// </summary>
            public bool? IsUpToDate( string key, long id )
            {
                if ( !Data.ContainsKey( key ) || !Data[key].ContainsKey( id ) )
                {
                    return null;
                }
                return DateTime.Now <= Data[key][id];
            }

            /// <summary>
            /// Removes and returns all entries whose expiration date is in the past.
            /// </summary>
            public IEnumerable<Tuple<string, long>> Cleanup()
            {
                var tooOld = Data.SelectMany( p => p.Value.Where( p2 => p2.Value < DateTime.Now )
                                                          .Select( p2 => Tuple.Create( p.Key, p2.Key ) ) )
                                 .ToArray();

                foreach ( var tuple in tooOld )
                {
                    Remove( tuple.Item1, tuple.Item2 );
                }

                return tooOld;
            }
        }
    }
}