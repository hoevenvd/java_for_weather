require 'soap/wsdlDriver'

log = Logger.new(STDOUT)
log.level = Logger::WARN

SRC = AppConfig.sync_src
src_soap = SOAP::WSDLDriverFactory.new(SRC).create_rpc_driver
log.debug(src_soap)

DEST = AppConfig.sync_dest
dest_soap = SOAP::WSDLDriverFactory.new(DEST).create_rpc_driver
log.debug(dest_soap)

error_count = 0

begin
  15.times do
    log.debug("getting conditions.")
    t = Time.now
    conditions = src_soap.GetCurrentConditions("01915")
    log.debug(conditions)
    log.debug(Time.now - t)
    log.debug("writing")
    t = Time.now
    dest_soap.PutCurrentConditions("wx", "01915", conditions)
    log.debug("done.")
    log.debug(Time.now - t)
    error_count = 0
    sleep 2
  end

  last_archive = dest_soap.GetLastArchive("01915")
  log.debug(last_archive.date)

  archive_structs = src_soap.GetArchiveSince("wx", "01915", last_archive.date)
  log.debug("returned #{archive_structs.length} entries")

  archive_structs.each do |s|
    t = Time.now
    dest_soap.PutArchiveEntry("wx", "01915", s)
    log.debug(s.date)
    log.debug(Time.now - t)
  end
rescue
  log.error($!)
  log.error("sleeping...")
  sleep 60
  exit 1 unless error_count < 25
  error_count += 1
  retry
end
