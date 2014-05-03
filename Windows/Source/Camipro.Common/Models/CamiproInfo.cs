using System.Runtime.Serialization;

namespace PocketCampus.Camipro.Models
{
    /// <summary>
    /// Information about a CAMIPRO account balance and e-banking.
    /// </summary>
    /// <remarks>
    /// This class is used to cache both an AccountInfo and an EbankingInfo, 
    /// Tuple can't be used because it's not serializable by DataContractSerializers.
    /// </remarks>
    [DataContract]
    public sealed class CamiproInfo
    {
        /// <summary>
        /// Gets or sets the account info.
        /// </summary>
        [DataMember]
        public AccountInfo AccountInfo { get; set; }

        /// <summary>
        /// Gets or sets the e-banking info.
        /// </summary>
        [DataMember]
        public EbankingInfo EbankingInfo { get; set; }


        /// <summary>
        /// Creates an empty CamiproInfo, for serialization purposes.
        /// </summary>
        public CamiproInfo() { }

        /// <summary>
        /// Creates a CamiproInfo with the specified account and e-banking info.
        /// </summary>
        public CamiproInfo( AccountInfo accountInfo, EbankingInfo ebankingInfo )
        {
            AccountInfo = accountInfo;
            EbankingInfo = ebankingInfo;
        }
    }
}