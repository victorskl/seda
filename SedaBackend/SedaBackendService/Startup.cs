using Microsoft.Owin;
using Owin;

[assembly: OwinStartup(typeof(SedaBackendService.Startup))]

namespace SedaBackendService
{
    public partial class Startup
    {
        public void Configuration(IAppBuilder app)
        {
            ConfigureMobileApp(app);
        }
    }
}