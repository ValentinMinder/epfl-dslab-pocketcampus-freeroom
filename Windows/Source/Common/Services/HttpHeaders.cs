// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using ThinMvvm;

namespace PocketCampus.Common.Services
{
    // TODO this is not very nice... an extension method on IServerSettings, perhaps? but then it could leak dictionaries...
    // putting it in IServerSettings is bad because it's not stored
    // conditionalweaktable would probably be too ugly.
    public class HttpHeaders : IHttpHeaders
    {
        private const string SessionHeaderName = "X-PC-AUTH-PCSESSID";
        private const string LanguageHeaderName = "X-PC-LANG-CODE";
        // TODO move that (server-side?)
        private static readonly string[] AvailableLanguages = { "en", "fr" };
        private const string DefaultLanguage = "en";

        private readonly IServerSettings _settings;

        private readonly Dictionary<string, string> _current;


        public IReadOnlyDictionary<string, string> Current
        {
            get { return _current; }
        }


        public HttpHeaders( IServerSettings settings )
        {
            _current = new Dictionary<string, string>();

            _settings = settings;
            _settings.ListenToProperty( x => x.Session, UpdateSessionHeader );
            UpdateSessionHeader();

            // The language header will not change for the app's lifetime
            string desiredLanguage = CultureInfo.CurrentUICulture.TwoLetterISOLanguageName;
            string language = AvailableLanguages.Contains( desiredLanguage ) ? desiredLanguage : DefaultLanguage;
            _current.Add( LanguageHeaderName, language );
        }


        private void UpdateSessionHeader()
        {
            if ( _current.ContainsKey( SessionHeaderName ) )
            {
                if ( _settings.Session == null )
                {
                    _current.Remove( SessionHeaderName );
                }
                else
                {
                    _current[SessionHeaderName] = _settings.Session;
                }
            }
            else if ( _settings.Session != null )
            {
                _current.Add( SessionHeaderName, _settings.Session );
            }
        }
    }
}