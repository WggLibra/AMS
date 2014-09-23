package org.ams.db;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class HibernateUtil {
	private static final Logger log = Logger.getLogger(HibernateUtil.class);
	private static SessionFactory sf;

	public static synchronized void initHibernate() {
		if (getSessionFactory() == null) {
			// Configuration conf = new Configuration().configure();
			Configuration configuration = new Configuration().configure();

			ServiceRegistry serviceRegistry = new ServiceRegistryBuilder()
					.applySettings(configuration.getProperties())
					.buildServiceRegistry();
			sf = configuration.buildSessionFactory(serviceRegistry);

			log.info("Hibernate initialized");
		}
	}

	private static SessionFactory getSessionFactory() {

		return sf;
	}

	public static Session getSession() {
		try {

			return getSessionFactory().getCurrentSession();
		} catch (HibernateException e) {

			return createNewSession();
		}
	}

	public static Session createNewSession() {
		return getSessionFactory().openSession();

	}

	public static void shutDown() {
		if (getSessionFactory() != null) {
			getSessionFactory().close();
			HibernateUtil.sf = null;
		}
		log.info("Hibernate sessionfactory has closed");

	}
}